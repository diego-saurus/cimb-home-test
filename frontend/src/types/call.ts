export type SentimentFilter = 'all' | 'BELOW_70' | 'AT_OR_ABOVE_70'

export interface CallRecord {
  callId: string
  callTimestamp: string
  csAgentName: string
  customerName: string
  id: number
  sentimentScore: number
}
