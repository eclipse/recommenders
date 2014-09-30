package org.eclipse.recommenders.internal.stacktraces.rcp.model;

import static com.google.common.base.Optional.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.Closeable;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.recommenders.internal.stacktraces.rcp.LogMessages;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.recommenders.utils.Openable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FilterService implements Openable, Closeable {

    private static final int MAX_HISTORY_LENGTH = 100;
    private static final int HISTORY_CLEARNUP_LENGTH = 30;

    private ModificationListener modificationListener = new ModificationListener();
    private File config;

    @VisibleForTesting
    IgnoreList ignores = ModelFactory.eINSTANCE.createIgnoreList();
    @VisibleForTesting
    Map<String, IgnoredEntry> fingerprints = Maps.newHashMap();

    public FilterService(File ignores) {
        this.config = ignores;
    }

    public boolean shouldSend(ErrorReport report) {
        String fingerprint = getFingerprint(report).orNull();
        if (fingerprint == null) {
            return false;
        }
        IgnoredEntry state = findOrCreateIgnoreEntry(fingerprint, 0);
        Calendar sentOn = state.getSentOn();
        Calendar weekAgo = Calendar.getInstance();
        weekAgo.add(Calendar.DAY_OF_MONTH, -7);
        return weekAgo.after(sentOn);
    }

    private IgnoredEntry findOrCreateIgnoreEntry(String fingerprint, long timeInMillis) {
        IgnoredEntry state = fingerprints.get(fingerprint);
        if (state == null) {
            state = ModelFactory.eINSTANCE.createIgnoredEntry();
            state.setFingerprint(fingerprint);
            Calendar sentOn = Calendar.getInstance();
            sentOn.setTimeInMillis(timeInMillis);
            state.setSentOn(sentOn);
            ignores.getSent().add(state);
            fingerprints.put(fingerprint, state);
        }
        return state;
    }

    private Optional<String> getFingerprint(ErrorReport report) {
        Status status = report.getStatus();
        if (status == null) {
            return absent();
        }
        String fingerprint = status.getFingerprint();
        if (StringUtils.isEmpty(fingerprint)) {
            return absent();
        }
        return of(fingerprint);
    }

    public void sent(ErrorReport report) {
        String fingerprint = getFingerprint(report).orNull();
        if (fingerprint == null) {
            return;
        }
        findOrCreateIgnoreEntry(fingerprint, System.currentTimeMillis());
        EList<IgnoredEntry> sent = ignores.getSent();
        // remove the first 30 entries if history reached upper limit:
        if (sent.size() > MAX_HISTORY_LENGTH) {
            List<IgnoredEntry> old = Lists.newLinkedList(sent.subList(0, HISTORY_CLEARNUP_LENGTH));
            sent.removeAll(old);
            for (IgnoredEntry e : old) {
                fingerprints.remove(e.getFingerprint());
            }
        }
        return;
    }

    public boolean modified() {
        return modificationListener.modified;
    }

    @Override
    public void open() {
        loadFromFile();
        registerModificationListener();
        createFingerprintIndex();
    }

    private void registerModificationListener() {
        ignores.eAdapters().add(modificationListener);
    }

    private void loadFromFile() {
        if (!config.exists()) {
            return;
        }
        try {
            URI url = URI.createFileURI(config.getAbsolutePath());
            XMIResourceImpl resource = new XMIResourceImpl(url);
            resource.load(Maps.newHashMap());
            ignores = (IgnoreList) Iterables.getFirst(resource.getContents(), ignores);
        } catch (Exception e) {
            Logs.log(LogMessages.LOAD_IGNORE_LIST_FAILED, e);
        }

    }

    private void createFingerprintIndex() {
        for (IgnoredEntry e : ignores.getSent()) {
            String fp = e.getFingerprint();
            if (!isEmpty(fp)) {
                fingerprints.put(fp, e);
            }
        }
    }

    @Override
    public void close() {
        saveToFile();
    }

    public void save() {
        saveToFile();
    }

    private void saveToFile() {
        try {
            URI url = URI.createFileURI(config.getAbsolutePath());
            XMIResourceImpl resource = new XMIResourceImpl(url);
            resource.getContents().add(ignores);
            resource.save(Maps.newHashMap());
        } catch (Exception e) {
            Logs.log(LogMessages.SAVE_IGNORE_LIST_FAILED, e);
        }
    }

    private final class ModificationListener extends AdapterImpl {
        boolean modified;

        @Override
        public void notifyChanged(Notification msg) {
            modified = true;
        }
    }

}
