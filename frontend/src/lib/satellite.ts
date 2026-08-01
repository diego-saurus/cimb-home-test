import axios from 'axios'

export const satellite = axios.create({
  baseURL: 'http://localhost:8080/api',
})
