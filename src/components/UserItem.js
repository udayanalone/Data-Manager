import React from 'react';

const UserItem = ({ user, onEdit, onDelete }) => {
    return (
        <div className="col-md-6 col-lg-4 mb-4">
            <div className="card h-100">
                <div className="card-body">
                    <h5 className="card-title">{user.name}</h5>
                    <h6 className="card-subtitle mb-2 text-muted">{user.role}</h6>

                    <div className="mb-2">
                        <strong>Email:</strong> {user.email}
                    </div>

                    {user.about && (
                        <div className="mb-2">
                            <strong>About:</strong> {user.about}
                        </div>
                    )}

                    <div className="mt-3">
                        <button
                            className="btn btn-sm btn-outline-primary me-2"
                            onClick={() => onEdit(user)}
                        >
                            Edit
                        </button>
                        <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => onDelete(user.id)}
                        >
                            Delete
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UserItem;
