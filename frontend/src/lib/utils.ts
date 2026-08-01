import type { AxiosResponse } from 'axios'

export const unwrap = <TResponse>(response: AxiosResponse<TResponse>) => response.data
