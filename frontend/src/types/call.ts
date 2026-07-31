export type SentimentFilter = 'all' | 'below_70' | 'above_70'

export interface CallRecord {
  id: string
  callId: string
  callTimestamp: string
  csName: string
  customerName: string
  sentimentScore: number
}
