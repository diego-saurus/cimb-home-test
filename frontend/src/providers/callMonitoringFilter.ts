import type { DateValue } from '@internationalized/date'

import { inject, type InjectionKey, type Ref } from 'vue'

export interface CallMonitoringFilterContext {
  startDate: Ref<DateValue | null>
  endDate: Ref<DateValue | null>
}

export const CallMonitoringFilterKey: InjectionKey<CallMonitoringFilterContext> = Symbol()

export function useCallMonitoringFilterContext(): CallMonitoringFilterContext {
  const context = inject(CallMonitoringFilterKey)

  if (!context) throw new Error('CallMonitoringFilter not provided')

  return context
}
