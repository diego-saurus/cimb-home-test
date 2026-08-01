import type { CalendarDate } from '@internationalized/date'

import { provide, ref } from 'vue'

import type { SentimentFilter } from '@/types/call'

import { CallMonitoringFilterKey, type CallMonitoringFilterContext } from '@/providers/callMonitoringFilter'

export type DateRangePayload = {
  startDate: CalendarDate | null
  endDate: CalendarDate | null
}

export function useCallMonitoringFilter(): CallMonitoringFilterContext {
  const startDate = ref(null)
  const endDate = ref(null)
  const sentiment = ref<SentimentFilter>('all')

  const reset = () => {
    startDate.value = null
    endDate.value = null
    sentiment.value = 'all'
  }

  const context: CallMonitoringFilterContext = { startDate, endDate, sentiment, reset }

  provide(CallMonitoringFilterKey, context)

  return context
}
