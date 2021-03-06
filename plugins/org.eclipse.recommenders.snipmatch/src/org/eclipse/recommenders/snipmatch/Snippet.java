/**
 * Copyright (c) 2013 Madhuranga Lakjeewa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Madhuranga Lakjeewa - initial API and implementation.
 *    Olav Lenz - change data structure of snippet
 *    Stefan Prisca - add property change support
 */
package org.eclipse.recommenders.snipmatch;

import static org.eclipse.recommenders.snipmatch.Location.FILE;
import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.recommenders.coordinates.ProjectCoordinate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * This class represent a snippet. This is used to serialize and de-serialize snippet with gson.
 */
public class Snippet implements ISnippet {

    public static final String FORMAT_VERSION = "format-5";

    private transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    @SerializedName("uuid")
    private UUID uuid;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("extraSearchTerms")
    private List<String> extraSearchTerms = new ArrayList<>();
    @SerializedName("tags")
    private List<String> tags = new ArrayList<>();
    @SerializedName("code")
    private String code;
    @SerializedName("location")
    private Location location = FILE;
    @SerializedName("filenameRestrictions")
    private List<String> filenameRestrictions = new ArrayList<>();
    @SerializedName("dependencies")
    private Set<ProjectCoordinate> neededDependencies = new HashSet<>();

    public Snippet() {
        this(UUID.randomUUID(), "", "", new ArrayList<String>(), new ArrayList<String>(), "", Location.NONE,
                new ArrayList<String>(), new HashSet<ProjectCoordinate>());
    }

    public Snippet(String code, Set<ProjectCoordinate> dependencies) {
        this(UUID.randomUUID(), "", "", new ArrayList<String>(), new ArrayList<String>(), code, Location.NONE,
                new ArrayList<String>(), dependencies);
    }

    public Snippet(UUID uuid, String name, String description, List<String> extraSearchTerms, List<String> tags,
            String code, Location location, List<String> filenameRestrictions, Set<ProjectCoordinate> neededDependencies) {
        ensureIsNotNull(uuid);
        ensureIsNotNull(name);
        ensureIsNotNull(description);
        ensureIsNotNull(extraSearchTerms);
        ensureIsNotNull(tags);
        ensureIsNotNull(code);
        ensureIsNotNull(location);
        ensureIsNotNull(neededDependencies);
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.extraSearchTerms = extraSearchTerms;
        this.tags = tags;
        this.code = code;
        this.location = location;
        this.filenameRestrictions = filenameRestrictions;
        this.neededDependencies = neededDependencies;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getExtraSearchTerms() {
        return ImmutableList.copyOf(extraSearchTerms);
    }

    @Override
    public List<String> getTags() {
        return ImmutableList.copyOf(tags);
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public List<String> getFilenameRestrictions() {
        return filenameRestrictions;
    }

    @Override
    public Set<ProjectCoordinate> getNeededDependencies() {
        return ImmutableSet.copyOf(neededDependencies);
    }

    public void setCode(String code) {
        firePropertyChange("code", this.code, this.code = code);
    }

    public void setName(String name) {
        firePropertyChange("name", this.name, this.name = name);
    }

    public void setDescription(String description) {
        firePropertyChange("description", this.description, this.description = description);
    }

    public void setNeededDependencies(Set<ProjectCoordinate> neededDependencies) {
        firePropertyChange("dependencies", this.neededDependencies, this.neededDependencies = neededDependencies);
    }

    public void setLocation(Location location) {
        firePropertyChange("location", this.location, this.location = location);
    }

    public void setFilenameRestrictions(List<String> filenameRestrictions) {
        firePropertyChange("filenameRestrictions", this.filenameRestrictions,
                this.filenameRestrictions = filenameRestrictions);
    }

    public void setExtraSearchTerms(List<String> extraSearchTerms) {
        firePropertyChange("extraSearchTerms", this.extraSearchTerms, this.extraSearchTerms = extraSearchTerms);

    }

    public void setTags(List<String> tags) {
        firePropertyChange("tags", this.tags, this.tags = tags);
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static Snippet copy(ISnippet snippet) {
        return new Snippet(snippet.getUuid(), snippet.getName(), snippet.getDescription(),
                snippet.getExtraSearchTerms(), snippet.getTags(), snippet.getCode(),
                snippet.getLocation() != null ? snippet.getLocation() : FILE, snippet.getFilenameRestrictions(),
                snippet.getNeededDependencies());
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
