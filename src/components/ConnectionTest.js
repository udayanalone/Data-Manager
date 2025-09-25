import React, { useState, useEffect } from 'react';
import UserService from '../services/UserService';

const ConnectionTest = () => {
    const [backendStatus, setBackendStatus] = useState('Testing...');
    const [userCount, setUserCount] = useState(0);

    useEffect(() => {
        testConnection();
    }, []);

    const testConnection = async () => {
        try {
            // Test backend connectivity
            const response = await UserService.getAllUsers();
            setUserCount(response.data.length);
            setBackendStatus('✅ Connected - Backend is responding');
        } catch (error) {
            setBackendStatus('❌ Connection failed - Backend not responding');
            console.error('Connection test failed:', error);
        }
    };

    return (
        <div className="container mt-4">
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-header">
                            <h5 className="mb-0">🔗 Connection Test</h5>
                        </div>
                        <div className="card-body text-center">
                            <div className="mb-3">
                                <h6>Frontend Status</h6>
                                <span className="badge bg-success">✅ React App Running</span>
                            </div>

                            <div className="mb-3">
                                <h6>Backend Status</h6>
                                <span className={backendStatus.includes('✅') ? 'badge bg-success' : 'badge bg-danger'}>
                                    {backendStatus}
                                </span>
                            </div>

                            <div className="mb-3">
                                <h6>Database Status</h6>
                                <span className="badge bg-info">📊 H2 Database Active</span>
                            </div>

                            <div className="mb-3">
                                <h6>Current Users</h6>
                                <span className="badge bg-secondary">{userCount} users in database</span>
                            </div>

                            <div className="mt-4">
                                <button
                                    className="btn btn-outline-primary"
                                    onClick={testConnection}
                                >
                                    🔄 Test Connection
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ConnectionTest;
