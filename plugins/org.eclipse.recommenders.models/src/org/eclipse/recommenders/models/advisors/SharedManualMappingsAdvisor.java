package org.eclipse.recommenders.models.advisors;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.recommenders.models.DependencyInfo;
import org.eclipse.recommenders.models.DependencyType;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelCoordinate;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.utils.Pair;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class SharedManualMappingsAdvisor extends AbstractProjectCoordinateAdvisor {

    private static final ModelCoordinate MAPPINGS = new ModelCoordinate("org.eclipse.recommenders", "mappings", null,
            "properties", "1.0.0");

    private final IModelRepository repository;

    private List<Pair<String, ProjectCoordinate>> mappings;

    public SharedManualMappingsAdvisor(IModelRepository repository) {
        this.repository = repository;
    }

    @Override
    protected boolean isApplicable(DependencyType any) {
        return true;
    }

    @Override
    protected Optional<ProjectCoordinate> doSuggest(DependencyInfo dependencyInfo) {
        initializeMappings();

        String path = dependencyInfo.getFile().getAbsolutePath().replace(File.separatorChar, '/');
        for (Pair<String, ProjectCoordinate> mapping : mappings) {
            String suffixPattern = mapping.getFirst();
            if (matchesSuffixPattern(path, suffixPattern)) {
                return Optional.of(mapping.getSecond());
            }
        }

        return Optional.absent();
    }

    @VisibleForTesting
    static boolean matchesSuffixPattern(String path, String suffixPattern) {
        int separators = StringUtils.countMatches(suffixPattern, "/") + 1;
        int separatorIndex = path.length();
        while (separators > 0) {
            separatorIndex = path.lastIndexOf("/", separatorIndex - 1);
            separators--;
        }
        String substring = path.substring(separatorIndex + 1);
        return FilenameUtils.wildcardMatch(substring, suffixPattern);
    }

    private synchronized void initializeMappings() {
        if (mappings == null) {
            Optional<File> mappingFile = repository.resolve(MAPPINGS, false);
            if (mappingFile.isPresent()) {
                mappings = readMappingFile(mappingFile.get());
            } else {
                mappings = Collections.emptyList();
            }
        }
    }

    private List<Pair<String, ProjectCoordinate>> readMappingFile(File mappingFile) {
        try {
            List<Pair<String, ProjectCoordinate>> result = Lists.newLinkedList();
            List<String> lines = Files.readLines(mappingFile, Charsets.UTF_8);
            for (String line : lines) {
                String[] split = StringUtils.split(line, "=");
                if (split.length != 2) {
                    throw new IllegalArgumentException();
                }
                result.add(Pair.newPair(split[0], ProjectCoordinate.valueOf(split[1])));
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
