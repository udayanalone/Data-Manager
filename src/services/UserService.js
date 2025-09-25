import axios from 'axios';

const API_BASE_URL = 'http://localhost:8082';

class UserService {
    getAllUsers() {
        return axios.get(`${API_BASE_URL}/get_user_data`);
    }

    getUserById(id) {
        return axios.get(`${API_BASE_URL}/get_user_data/${id}`);
    }

    getUserByName(name) {
        return axios.get(`${API_BASE_URL}/get_user_name/name/${name}`);
    }

    createUser(user) {
        return axios.post(`${API_BASE_URL}/save_user_data`, user);
    }

    updateUser(id, user) {
        return axios.put(`${API_BASE_URL}/update_user_data/${id}`, user);
    }

    deleteUser(id) {
        return axios.delete(`${API_BASE_URL}/delete_user_data/${id}`);
    }

    // Search and Filter methods
    searchUsers(searchParams) {
        return axios.get(`${API_BASE_URL}/api/users/search`, { params: searchParams });
    }

    filterUsers(filterParams) {
        return axios.get(`${API_BASE_URL}/api/users/filter`, { params: filterParams });
    }

    // Import/Export methods
    importUsers(file) {
        const formData = new FormData();
        formData.append('file', file);
        return axios.post(`${API_BASE_URL}/api/users/import`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
    }

    exportUsersToCsv() {
        return axios.get(`${API_BASE_URL}/api/users/export/csv`, {
            responseType: 'blob',
        }).then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'users.csv');
            document.body.appendChild(link);
            link.click();
            link.remove();
        });
    }

    exportUsersToVCard() {
        return axios.get(`${API_BASE_URL}/api/users/export/vcard`, {
            responseType: 'blob',
        }).then(response => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'contacts.vcf');
            document.body.appendChild(link);
            link.click();
            link.remove();
        });
    }
}

const userService = new UserService();
export default userService;
