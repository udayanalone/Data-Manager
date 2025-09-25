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
}

export default new UserService();
