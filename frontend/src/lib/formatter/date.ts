import { DateFormatter } from '@internationalized/date'

export const dateMedium = new DateFormatter('id-ID', { dateStyle: 'medium' })
export const dateLong = new DateFormatter('id-ID', { dateStyle: 'long', timeStyle: 'short' })
