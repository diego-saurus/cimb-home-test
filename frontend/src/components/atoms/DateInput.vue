<script setup lang="ts">
import type { CalendarProps } from '@nuxt/ui'

import { getLocalTimeZone, type DateValue } from '@internationalized/date'

import { dateMedium } from '@/lib/formatter/date'

withDefaults(
  defineProps<
    CalendarProps & {
      triggerPlaceholder?: string
    }
  >(),
  {
    triggerPlaceholder: 'Select a date',
  }
)
const date = defineModel<DateValue>('date')
</script>

<template>
  <UPopover>
    <UButton color="neutral" variant="subtle" icon="i-lucide-calendar">
      {{ date ? dateMedium.format(date.toDate(getLocalTimeZone())) : triggerPlaceholder }}
    </UButton>

    <template #content>
      <UCalendar v-bind="$props" v-model="date" class="p-2" />
    </template>
  </UPopover>
</template>
