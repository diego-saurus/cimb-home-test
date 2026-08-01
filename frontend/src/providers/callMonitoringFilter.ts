import type { DateValue } from '@internationalized/date'

import { inject, type InjectionKey, type Ref } from 'vue'

import type { SentimentFilter } from '@/types/call'

export interface CallMonitoringFilterContext {
  startDate: Ref<DateValue | null>
  endDate: Ref<DateValue | null>
  sentiment: Ref<SentimentFilter>
  reset: () => void
}

export const CallMonitoringFilterKey: InjectionKey<CallMonitoringFilterContext> = Symbol()

export function useCallMonitoringFilterContext(): CallMonitoringFilterContext {
  const context = inject(CallMonitoringFilterKey)

  if (!context) throw new Error('CallMonitoringFilter not provided')

  return context
}
