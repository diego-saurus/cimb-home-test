<script lang="ts">
import type { DateValue } from '@internationalized/date'

import { CalendarDate, getLocalTimeZone, today } from '@internationalized/date'
import { breakpointsTailwind, useBreakpoints } from '@vueuse/core'
import { computed, ref, shallowRef, watch } from 'vue'

import { dateMedium } from '@/lib/formatter/date'

type DateRange = { start: DateValue | undefined; end: DateValue | undefined }
type Props = { triggerPlaceholder?: string }

const MAX_MONTHS_LOOKBACK = 3

const presets = [
  { label: 'Last 7 days', days: 7 },
  { label: 'Last 14 days', days: 14 },
  { label: 'Last 1 month', months: 1 },
  { label: 'Last 2 month', months: 2 },
  { label: 'Last 3 months', months: MAX_MONTHS_LOOKBACK },
]
</script>

<template>
  <UPopover v-model:open="isOpen">
    <UButton
      color="neutral"
      variant="outline"
      size="lg"
      :icon="startDate ? 'i-ph:calendar-check' : 'i-ph:calendar-dots'"
      :aria-label="triggerLabel"
      :ui="{ leadingIcon: 'text-dimmed' }"
    >
      {{ triggerLabel }}
    </UButton>

    <template #content>
      <div class="flex flex-col">
        <div class="flex items-stretch divide-x divide-default">
          <div class="hidden sm:flex flex-col justify-center py-2">
            <UButton
              v-for="range in presets"
              :key="range.label"
              :label="range.label"
              color="secondary"
              variant="ghost"
              class="rounded-none px-4"
              :class="[isPresetSelected(range) ? 'bg-elevated' : 'hover:bg-elevated/50']"
              truncate
              @click="selectPreset(range)"
            />
          </div>

          <UCalendar
            v-model="draft"
            class="p-2"
            :year-controls="false"
            :view-control="false"
            :number-of-months="isDesktop ? 2 : 1"
            :min-value="minValue"
            :max-value="maxValue"
            :is-date-unavailable="isDateUnavailable"
            :placeholder="initialPlaceholder"
            range
          />
        </div>

        <div class="flex items-center justify-end gap-2 border-t border-default p-2">
          <UButton color="neutral" variant="ghost" :disabled="!draft.start && !draft.end" @click="reset">
            Reset
          </UButton>
          <UButton color="secondary" :disabled="!canApply" @click="apply"> Apply </UButton>
        </div>
      </div>
    </template>
  </UPopover>
</template>

<script setup lang="ts">
withDefaults(defineProps<Props>(), {
  triggerPlaceholder: 'Select date range',
})

const startDate = defineModel<DateValue | null>('startDate', { default: null })
const endDate = defineModel<DateValue | null>('endDate', { default: null })

const tz = getLocalTimeZone()
const todayValue = today(tz)

const minValue = todayValue.subtract({ months: MAX_MONTHS_LOOKBACK })
const maxValue = todayValue
const initialPlaceholder = todayValue.subtract({ months: 1 })

function isDateUnavailable(date: DateValue): boolean {
  return date.compare(minValue) < 0 || date.compare(maxValue) > 0
}

const breakpoints = useBreakpoints(breakpointsTailwind)
const isDesktop = breakpoints.greaterOrEqual('sm')

const isOpen = ref(false)
const draft = shallowRef<DateRange>({ start: undefined, end: undefined })

watch(
  [startDate, endDate, isOpen],
  ([start, end]) => {
    draft.value = { start: start ?? undefined, end: end ?? undefined }
  },
  { immediate: true }
)

function selectPreset(range: (typeof presets)[number]) {
  const end = today(tz)
  const start = end.subtract({ days: range.days, months: range.months })
  draft.value = { start, end }
}

function isPresetSelected(range: (typeof presets)[number]): boolean {
  const { start, end } = draft.value
  if (!start || !end) return false
  const expectedEnd = today(tz)
  const expectedStart = expectedEnd.subtract({ days: range.days, months: range.months })
  return start.compare(expectedStart) === 0 && end.compare(expectedEnd) === 0
}

function normalize(range: DateRange): DateRange {
  const { start, end } = range
  if (start && end && start.compare(end) > 0) {
    return { start: end, end: start }
  }
  return range
}

const canApply = computed(() => {
  const { start, end } = draft.value
  if (!start || !end) return false
  return true
})

function apply() {
  if (!canApply.value) return
  const normalized = normalize(draft.value)
  draft.value = normalized
  startDate.value = normalized.start ?? null
  endDate.value = normalized.end ?? null
  isOpen.value = false
}

function reset() {
  draft.value = { start: undefined, end: undefined }
  startDate.value = null
  endDate.value = null
}

const triggerLabel = computed(() => {
  const { start, end } = draft.value
  if (!start && !end) return 'Select date range'
  if (start && !end) return dateMedium.format(start.toDate(tz))
  if (!start && end) return dateMedium.format(end.toDate(tz))
  return `${dateMedium.format((start as CalendarDate).toDate(tz))} - ${dateMedium.format((end as CalendarDate).toDate(tz))}`
})
</script>
