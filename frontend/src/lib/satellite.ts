import axios from 'axios'

import { SERVER_URL } from '@/lib/environment'

export const satellite = axios.create({
  baseURL: `${SERVER_URL}`,
})
