import React, { useState } from 'react';
import UserService from '../services/UserService';

const SearchFilterBar = ({ onSearch, onFilter, onImport, onExportCsv, onExportVCard }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchField, setSearchField] = useState('name');
    const [filterRole, setFilterRole] = useState('');
    const [sortBy, setSortBy] = useState('name');
    const [sortOrder, setSortOrder] = useState('asc');
    const [importFile, setImportFile] = useState(null);
    const [importMessage, setImportMessage] = useState('');

    const handleSearch = () => {
        const searchParams = {
            name: searchField === 'name' ? searchTerm : '',
            email: searchField === 'email' ? searchTerm : '',
            role: searchField === 'role' ? searchTerm : '',
            about: searchField === 'about' ? searchTerm : ''
        };
        onSearch(searchParams);
    };

    const handleFilter = () => {
        onFilter({ role: filterRole, sortBy, sortOrder });
    };

    const handleImport = async (e) => {
        e.preventDefault();
        if (!importFile) {
            setImportMessage('Please select a file to import');
            return;
        }

        try {
            const response = await UserService.importUsers(importFile);
            setImportMessage(response.data);
            setImportFile(null);
            // Refresh the user list
            window.location.reload();
        } catch (error) {
            setImportMessage('Import failed: ' + (error.response?.data || error.message));
        }
    };

    const handleExport = (format) => {
        if (format === 'csv') {
            onExportCsv();
        } else if (format === 'vcard') {
            onExportVCard();
        }
    };

    return (
        <div className="search-filter-section mb-4">
            <div className="row g-3">
                {/* Search Section */}
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h6 className="mb-0">🔍 Advanced Search</h6>
                        </div>
                        <div className="card-body">
                            <div className="row g-2">
                                <div className="col-md-4">
                                    <select
                                        className="form-select"
                                        value={searchField}
                                        onChange={(e) => setSearchField(e.target.value)}
                                    >
                                        <option value="name">Name</option>
                                        <option value="email">Email</option>
                                        <option value="role">Role</option>
                                        <option value="about">About</option>
                                    </select>
                                </div>
                                <div className="col-md-6">
                                    <input
                                        type="text"
                                        className="form-control"
                                        placeholder={`Search by ${searchField}...`}
                                        value={searchTerm}
                                        onChange={(e) => setSearchTerm(e.target.value)}
                                        onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                                    />
                                </div>
                                <div className="col-md-2">
                                    <button
                                        className="btn btn-primary w-100"
                                        onClick={handleSearch}
                                    >
                                        <span className="me-1">🔍</span>
                                        Search
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Filter Section */}
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h6 className="mb-0">🔧 Smart Filters</h6>
                        </div>
                        <div className="card-body">
                            <div className="row g-2">
                                <div className="col-md-4">
                                    <select
                                        className="form-select"
                                        value={filterRole}
                                        onChange={(e) => setFilterRole(e.target.value)}
                                    >
                                        <option value="">All Roles</option>
                                        <option value="Admin">Admin</option>
                                        <option value="Manager">Manager</option>
                                        <option value="Developer">Developer</option>
                                        <option value="User">User</option>
                                    </select>
                                </div>
                                <div className="col-md-3">
                                    <select
                                        className="form-select"
                                        value={sortBy}
                                        onChange={(e) => setSortBy(e.target.value)}
                                    >
                                        <option value="name">Name</option>
                                        <option value="email">Email</option>
                                        <option value="role">Role</option>
                                        <option value="id">ID</option>
                                    </select>
                                </div>
                                <div className="col-md-3">
                                    <select
                                        className="form-select"
                                        value={sortOrder}
                                        onChange={(e) => setSortOrder(e.target.value)}
                                    >
                                        <option value="asc">↑ Ascending</option>
                                        <option value="desc">↓ Descending</option>
                                    </select>
                                </div>
                                <div className="col-md-2">
                                    <button
                                        className="btn btn-success w-100"
                                        onClick={handleFilter}
                                    >
                                        <span className="me-1">🔧</span>
                                        Filter
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Import/Export Section */}
            <div className="row g-3 mt-3">
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h6 className="mb-0">📥 Import Users</h6>
                        </div>
                        <div className="card-body">
                            <form onSubmit={handleImport}>
                                <div className="row g-2">
                                    <div className="col-md-8">
                                        <input
                                            type="file"
                                            className="form-control"
                                            accept=".csv,.vcf"
                                            onChange={(e) => setImportFile(e.target.files[0])}
                                        />
                                        <small className="text-muted">Supports CSV and vCard files</small>
                                    </div>
                                    <div className="col-md-4">
                                        <button
                                            type="submit"
                                            className="btn btn-info w-100"
                                        >
                                            <span className="me-1">📥</span>
                                            Import
                                        </button>
                                    </div>
                                </div>
                            </form>
                            {importMessage && (
                                <div className={`alert mt-2 ${importMessage.includes('Successfully') ? 'alert-success' : 'alert-danger'}`}>
                                    <span className="me-1">{importMessage.includes('Successfully') ? '✅' : '❌'}</span>
                                    {importMessage}
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h6 className="mb-0">📤 Export Users</h6>
                        </div>
                        <div className="card-body">
                            <div className="row g-2">
                                <div className="col-md-6">
                                    <button
                                        className="btn btn-outline-primary w-100"
                                        onClick={() => handleExport('csv')}
                                    >
                                        <span className="me-1">📊</span>
                                        Export CSV
                                    </button>
                                </div>
                                <div className="col-md-6">
                                    <button
                                        className="btn btn-outline-secondary w-100"
                                        onClick={() => handleExport('vcard')}
                                    >
                                        <span className="me-1">📱</span>
                                        Export vCard
                                    </button>
                                </div>
                            </div>
                            <small className="text-muted d-block mt-2">
                                Export all users in CSV or vCard format for easy migration
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SearchFilterBar;
