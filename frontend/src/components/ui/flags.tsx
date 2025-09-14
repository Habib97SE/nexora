"use client"

import { cn } from "@/lib/utils"

interface FlagProps {
  className?: string
}

export function FranceFlag({ className }: FlagProps) {
  return (
    <svg
      viewBox="0 0 9 6"
      className={cn("w-4 h-3", className)}
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect width="3" height="6" fill="#002395" />
      <rect x="3" width="3" height="6" fill="#FFFFFF" />
      <rect x="6" width="3" height="6" fill="#ED2939" />
    </svg>
  )
}

export function GermanyFlag({ className }: FlagProps) {
  return (
    <svg
      viewBox="0 0 9 6"
      className={cn("w-4 h-3", className)}
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect width="9" height="2" fill="#000000" />
      <rect y="2" width="9" height="2" fill="#DD0000" />
      <rect y="4" width="9" height="2" fill="#FFCE00" />
    </svg>
  )
}

export function USFlag({ className }: FlagProps) {
  return (
    <svg
      viewBox="0 0 9 6"
      className={cn("w-4 h-3", className)}
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect width="9" height="6" fill="#B22234" />
      <rect width="9" height="0.46" fill="#FFFFFF" />
      <rect y="0.92" width="9" height="0.46" fill="#FFFFFF" />
      <rect y="1.84" width="9" height="0.46" fill="#FFFFFF" />
      <rect y="2.76" width="9" height="0.46" fill="#FFFFFF" />
      <rect y="3.68" width="9" height="0.46" fill="#FFFFFF" />
      <rect y="4.6" width="9" height="0.46" fill="#FFFFFF" />
      <rect width="3.6" height="3.22" fill="#3C3B6E" />
    </svg>
  )
}

export function VietnamFlag({ className }: FlagProps) {
  return (
    <svg
      viewBox="0 0 9 6"
      className={cn("w-4 h-3", className)}
      xmlns="http://www.w3.org/2000/svg"
    >
      <rect width="9" height="6" fill="#DA020E" />
      <polygon
        points="4.5,1.5 4.1,2.3 5.2,2.3"
        fill="#FFFF00"
      />
      <polygon
        points="4.5,1.5 3.8,2.3 5.2,2.3"
        fill="#FFFF00"
      />
      <polygon
        points="4.5,1.5 3.2,1.8 5.8,1.8"
        fill="#FFFF00"
      />
      <polygon
        points="4.5,1.5 3.8,1.2 5.2,1.2"
        fill="#FFFF00"
      />
    </svg>
  )
}
