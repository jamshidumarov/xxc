import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('xxc_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('xxc_token')
    }
    return Promise.reject(err)
  }
)

export const productApi = {
  getAll: (params) => api.get('/products', { params }),
  getById: (id) => api.get(`/products/${id}`),
}

export const portfolioApi = {
  getAll: (params) => api.get('/portfolio', { params }),
  getFeatured: () => api.get('/portfolio/featured'),
  getById: (id) => api.get(`/portfolio/${id}`),
}

export const contactApi = {
  submit: (formData) => api.post('/contacts', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
}

export const certificateApi = {
  getAll: (params) => api.get('/certificates', { params }),
}

export default api
