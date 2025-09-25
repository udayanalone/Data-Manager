import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import UserList from './components/UserList';
import ConnectionTest from './components/ConnectionTest';
import './App.css';

function App() {
    return (
        <Router>
            <div className="App">
                <nav className="navbar navbar-expand-lg">
                    <div className="container">
                        <Link className="navbar-brand" to="/">
                            <span className="gradient-text">📊 DataManager</span>
                        </Link>
                        <button
                            className="navbar-toggler"
                            type="button"
                            data-bs-toggle="collapse"
                            data-bs-target="#navbarNav"
                        >
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="collapse navbar-collapse" id="navbarNav">
                            <ul className="navbar-nav me-auto">
                                <li className="nav-item">
                                    <Link className="nav-link" to="/">
                                        <span className="nav-icon">🏠</span> Home
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/users">
                                        <span className="nav-icon">👥</span> Users
                                    </Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link" to="/test">
                                        <span className="nav-icon">🔗</span> Connection Test
                                    </Link>
                                </li>
                            </ul>
                            <ul className="navbar-nav">
                                <li className="nav-item">
                                    <a
                                        className="nav-link external-link"
                                        href="http://localhost:8082/h2-console"
                                        target="_blank"
                                        rel="noopener noreferrer"
                                    >
                                        <span className="nav-icon">🗄️</span> Database
                                    </a>
                                </li>
                                <li className="nav-item">
                                    <a
                                        className="nav-link external-link"
                                        href="http://localhost:8082/"
                                        target="_blank"
                                        rel="noopener noreferrer"
                                    >
                                        <span className="nav-icon">🔌</span> API
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </nav>

                <main className="container-fluid py-5">
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/users" element={<UserList />} />
                        <Route path="/test" element={<ConnectionTest />} />
                    </Routes>
                </main>

                <footer className="text-center py-4 mt-5">
                    <div className="container">
                        <div className="row">
                            <div className="col-md-6">
                                <small className="text-muted">
                                    🚀 Built with React & Spring Boot
                                </small>
                            </div>
                            <div className="col-md-6">
                                <small className="text-muted">
                                    💫 Creative & Modern Design
                                </small>
                            </div>
                        </div>
                    </div>
                </footer>
            </div>
        </Router>
    );
}

const Home = () => {
    return (
        <div className="container mt-4">
            <div className="row justify-content-center">
                <div className="col-lg-10">
                    <div className="hero-section text-center mb-5">
                        <h1 className="display-4 mb-4 animate-fade-in">
                            Welcome to DataManager
                        </h1>
                        <p className="lead mb-5 animate-fade-in-delay">
                            A stunning user management system with modern design and seamless functionality
                        </p>

                        <div className="hero-cta">
                            <Link to="/users" className="btn btn-primary btn-lg me-3 animate-bounce">
                                <span className="me-2">👥</span>
                                Explore Users
                            </Link>
                            <Link to="/test" className="btn btn-outline-primary btn-lg animate-pulse">
                                <span className="me-2">🔗</span>
                                Test Connection
                            </Link>
                        </div>
                    </div>

                    <div className="features-grid">
                        <div className="row g-4">
                            <div className="col-md-4">
                                <div className="feature-card">
                                    <div className="feature-icon">
                                        👥
                                    </div>
                                    <h3 className="feature-title">User Management</h3>
                                    <p className="feature-description">
                                        Create, edit, and manage users with our intuitive interface
                                    </p>
                                    <Link to="/users" className="btn btn-primary">
                                        Manage Users
                                    </Link>
                                </div>
                            </div>

                            <div className="col-md-4">
                                <div className="feature-card">
                                    <div className="feature-icon">
                                        ⚡
                                    </div>
                                    <h3 className="feature-title">Real-time Updates</h3>
                                    <p className="feature-description">
                                        Experience instant updates with React's reactive system
                                    </p>
                                    <Link to="/test" className="btn btn-success">
                                        Test Connection
                                    </Link>
                                </div>
                            </div>

                            <div className="col-md-4">
                                <div className="feature-card">
                                    <div className="feature-icon">
                                        🎨
                                    </div>
                                    <h3 className="feature-title">Beautiful Design</h3>
                                    <p className="feature-description">
                                        Modern, responsive design with glass morphism effects
                                    </p>
                                    <a
                                        href="http://localhost:8082/h2-console"
                                        className="btn btn-info"
                                        target="_blank"
                                        rel="noopener noreferrer"
                                    >
                                        View Database
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="stats-section mt-5">
                        <div className="row text-center">
                            <div className="col-md-3">
                                <div className="stat-card">
                                    <div className="stat-number">⚛️</div>
                                    <div className="stat-label">React Frontend</div>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="stat-card">
                                    <div className="stat-number">🚀</div>
                                    <div className="stat-label">Spring Boot</div>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="stat-card">
                                    <div className="stat-number">🗄️</div>
                                    <div className="stat-label">H2 Database</div>
                                </div>
                            </div>
                            <div className="col-md-3">
                                <div className="stat-card">
                                    <div className="stat-number">🎨</div>
                                    <div className="stat-label">Modern UI</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default App;
