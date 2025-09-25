import React, { useState, useEffect } from 'react';
import UserService from '../services/UserService';
import UserForm from './UserForm';
import UserItem from './UserItem';
import SearchFilterBar from './SearchFilterBar';

const UserList = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [searchMode, setSearchMode] = useState(false);
    const [searchResults, setSearchResults] = useState([]);

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        try {
            setLoading(true);
            const response = await UserService.getAllUsers();
            setUsers(response.data);
            setError(null);
            setSearchMode(false);
        } catch (error) {
            setError('Failed to load users');
            console.error('Error loading users:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async (searchParams) => {
        try {
            setLoading(true);
            const response = await UserService.searchUsers(searchParams);
            setSearchResults(response.data);
            setSearchMode(true);
            setError(null);
        } catch (error) {
            setError('Search failed: ' + (error.response?.data || error.message));
            console.error('Error searching users:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFilter = async (filterParams) => {
        try {
            setLoading(true);
            const response = await UserService.filterUsers(filterParams);
            setUsers(response.data);
            setSearchMode(false);
            setError(null);
        } catch (error) {
            setError('Filter failed: ' + (error.response?.data || error.message));
            console.error('Error filtering users:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateUser = async (userData) => {
        try {
            await UserService.createUser(userData);
            setShowForm(false);
            loadUsers();
        } catch (error) {
            setError('Failed to create user');
            console.error('Error creating user:', error);
        }
    };

    const handleUpdateUser = async (id, userData) => {
        try {
            await UserService.updateUser(id, userData);
            setEditingUser(null);
            loadUsers();
        } catch (error) {
            setError('Failed to update user');
            console.error('Error updating user:', error);
        }
    };

    const handleDeleteUser = async (id) => {
        if (window.confirm('Are you sure you want to delete this user?')) {
            try {
                await UserService.deleteUser(id);
                loadUsers();
            } catch (error) {
                setError('Failed to delete user');
                console.error('Error deleting user:', error);
            }
        }
    };

    const handleEditUser = (user) => {
        setEditingUser(user);
        setShowForm(true);
    };

    const handleCancelEdit = () => {
        setEditingUser(null);
        setShowForm(false);
    };

    const handleExportCsv = async () => {
        try {
            await UserService.exportUsersToCsv();
        } catch (error) {
            setError('Export failed');
            console.error('Error exporting CSV:', error);
        }
    };

    const handleExportVCard = async () => {
        try {
            await UserService.exportUsersToVCard();
        } catch (error) {
            setError('Export failed');
            console.error('Error exporting vCard:', error);
        }
    };

    const displayUsers = searchMode ? searchResults : users;

    if (loading) {
        return (
            <div className="container mt-4">
                <div className="text-center">
                    <div className="spinner-border" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                    <p className="mt-2">Loading users...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2>User Management</h2>
                    <small className="text-muted">
                        {searchMode ? `${searchResults.length} search results` : `${users.length} total users`}
                    </small>
                </div>
                <button
                    className="btn btn-primary"
                    onClick={() => setShowForm(true)}
                >
                    <span className="me-2">➕</span>
                    Add New User
                </button>
            </div>

            {/* Search and Filter Bar */}
            <SearchFilterBar
                onSearch={handleSearch}
                onFilter={handleFilter}
                onImport={() => {}}
                onExportCsv={handleExportCsv}
                onExportVCard={handleExportVCard}
            />

            {error && (
                <div className="alert alert-danger" role="alert">
                    <span className="me-2">⚠️</span>
                    {error}
                </div>
            )}

            {showForm && (
                <div className="card mb-4">
                    <div className="card-header">
                        <h5>{editingUser ? 'Edit User' : 'Create New User'}</h5>
                    </div>
                    <div className="card-body">
                        <UserForm
                            user={editingUser}
                            onSubmit={editingUser ? handleUpdateUser : handleCreateUser}
                            onCancel={handleCancelEdit}
                        />
                    </div>
                </div>
            )}

            <div className="card">
                <div className="card-header d-flex justify-content-between align-items-center">
                    <h5>
                        {searchMode ? 'Search Results' : 'All Users'}
                        <span className="badge bg-secondary ms-2">{displayUsers.length}</span>
                    </h5>
                    {!searchMode && (
                        <button
                            className="btn btn-outline-primary btn-sm"
                            onClick={loadUsers}
                        >
                            <span className="me-1">🔄</span>
                            Refresh
                        </button>
                    )}
                </div>
                <div className="card-body">
                    {displayUsers.length === 0 ? (
                        <div className="text-center py-5">
                            <div className="mb-3">📝</div>
                            <h5 className="text-muted">
                                {searchMode ? 'No users found matching your search criteria' : 'No users found'}
                            </h5>
                            <p className="text-muted">
                                {searchMode
                                    ? 'Try adjusting your search terms or filters'
                                    : 'Create your first user to get started!'
                                }
                            </p>
                            {!searchMode && (
                                <button
                                    className="btn btn-primary"
                                    onClick={() => setShowForm(true)}
                                >
                                    Create First User
                                </button>
                            )}
                        </div>
                    ) : (
                        <div className="row">
                            {displayUsers.map(user => (
                                <UserItem
                                    key={user.id}
                                    user={user}
                                    onEdit={handleEditUser}
                                    onDelete={handleDeleteUser}
                                />
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default UserList;
