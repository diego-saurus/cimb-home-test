import {
  CalendarDate,
  getLocalTimeZone,
  type DateValue,
} from '@internationalized/date'

export function toIsoDate(value: DateValue | null | undefined): string | null {
  if (!value) return null
  return value.toDate(getLocalTimeZone()).toISOString().slice(0, 10)
}

export function fromIsoDate(value: string | null | undefined): DateValue | null {
  if (!value) return null
  const [year, month, day] = value.split('-').map(Number)
  if (!year || !month || !day) return null
  return new CalendarDate(year, month, day)
}