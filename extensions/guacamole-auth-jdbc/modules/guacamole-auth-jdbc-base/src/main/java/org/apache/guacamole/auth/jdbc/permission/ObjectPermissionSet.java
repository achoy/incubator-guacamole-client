/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.guacamole.auth.jdbc.permission;

import org.apache.guacamole.auth.jdbc.user.ModeledUser;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.apache.guacamole.auth.jdbc.user.ModeledAuthenticatedUser;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.jdbc.base.RestrictedObject;
import org.apache.guacamole.net.auth.permission.ObjectPermission;

/**
 * A database implementation of ObjectPermissionSet which uses an injected
 * service to query and manipulate the object-level permissions associated with
 * a particular user.
 *
 * @author Michael Jumper
 */
public abstract class ObjectPermissionSet extends RestrictedObject
    implements org.apache.guacamole.net.auth.permission.ObjectPermissionSet {

    /**
     * The user associated with this permission set. Each of the permissions in
     * this permission set is granted to this user.
     */
    private ModeledUser user;

    /**
     * Creates a new ObjectPermissionSet. The resulting permission set
     * must still be initialized by a call to init(), or the information
     * necessary to read and modify this set will be missing.
     */
    public ObjectPermissionSet() {
    }

    /**
     * Initializes this permission set with the current user and the user
     * to whom the permissions in this set are granted.
     *
     * @param currentUser
     *     The user who queried this permission set, and whose permissions
     *     dictate the access level of all operations performed on this set.
     *
     * @param user
     *     The user to whom the permissions in this set are granted.
     */
    public void init(ModeledAuthenticatedUser currentUser, ModeledUser user) {
        super.init(currentUser);
        this.user = user;
    }

    /**
     * Returns an ObjectPermissionService implementation for manipulating the
     * type of permissions contained within this permission set.
     *
     * @return
     *     An object permission service for manipulating the type of
     *     permissions contained within this permission set.
     */
    protected abstract ObjectPermissionService getObjectPermissionService();
 
    @Override
    public Set<ObjectPermission> getPermissions() throws GuacamoleException {
        return getObjectPermissionService().retrievePermissions(getCurrentUser(), user);
    }

    @Override
    public boolean hasPermission(ObjectPermission.Type permission,
            String identifier) throws GuacamoleException {
        return getObjectPermissionService().retrievePermission(getCurrentUser(), user, permission, identifier) != null;
    }

    @Override
    public void addPermission(ObjectPermission.Type permission,
            String identifier) throws GuacamoleException {
        addPermissions(Collections.singleton(new ObjectPermission(permission, identifier)));
    }

    @Override
    public void removePermission(ObjectPermission.Type permission,
            String identifier) throws GuacamoleException {
        removePermissions(Collections.singleton(new ObjectPermission(permission, identifier)));
    }

    @Override
    public Collection<String> getAccessibleObjects(Collection<ObjectPermission.Type> permissions,
            Collection<String> identifiers) throws GuacamoleException {
        return getObjectPermissionService().retrieveAccessibleIdentifiers(getCurrentUser(), user, permissions, identifiers);
    }

    @Override
    public void addPermissions(Set<ObjectPermission> permissions)
            throws GuacamoleException {
        getObjectPermissionService().createPermissions(getCurrentUser(), user, permissions);
    }

    @Override
    public void removePermissions(Set<ObjectPermission> permissions)
            throws GuacamoleException {
        getObjectPermissionService().deletePermissions(getCurrentUser(), user, permissions);
    }

}
