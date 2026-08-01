import type { CalendarDate } from '@internationalized/date'

import { provide, ref } from 'vue'

import { CallMonitoringFilterKey, type CallMonitoringFilterContext } from '@/providers/callMonitoringFilter'

export type DateRangePayload = {
  startDate: CalendarDate | null
  endDate: CalendarDate | null
}

export function useCallMonitoringFilter(): CallMonitoringFilterContext {
  const startDate = ref(null)
  const endDate = ref(null)

  const context: CallMonitoringFilterContext = { startDate, endDate }

  provide(CallMonitoringFilterKey, context)

  return context
}

