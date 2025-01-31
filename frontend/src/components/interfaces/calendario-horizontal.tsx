"use client"

import { useState } from "react"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import { format, addDays, startOfWeek } from "date-fns"
import { es } from "date-fns/locale"

interface CalendarioHorizontalProps {
    onSelectDate: (date: Date) => void
}

export function CalendarioHorizontal({ onSelectDate }: CalendarioHorizontalProps) {
    const [currentDate, setCurrentDate] = useState(new Date())

    const daysToShow = 7
    const startDate = startOfWeek(currentDate, { locale: es })
    const dates = Array.from({ length: daysToShow }, (_, i) => addDays(startDate, i))

    const handlePrevWeek = () => {
        setCurrentDate(addDays(currentDate, -7))
    }

    const handleNextWeek = () => {
        setCurrentDate(addDays(currentDate, 7))
    }

    return (
        <div className="flex items-center justify-center mx-auto space-x-4">
            <Button variant="outline" size="icon" onClick={handlePrevWeek}>
                <ChevronLeft className="h-4 w-4" />
            </Button>
            <div className="flex space-x-2 overflow-x-auto">
                {dates.map((date) => (
                    <Button
                        key={date.toISOString()}
                        variant="outline"
                        className={cn(
                            "flex flex-col items-center h-24 min-w-[90px]",
                            date.toDateString() === new Date().toDateString() && "border-primary",
                        )}
                        onClick={() => onSelectDate(date)}
                    >
                        <span className="text-xs">{format(date, "EEE", { locale: es })}</span>
                        <span className="text-lg font-bold">{format(date, "d", { locale: es })}</span>
                    </Button>
                ))}
            </div>
            <Button variant="outline" size="icon" onClick={handleNextWeek}>
                <ChevronRight className="h-4 w-4" />
            </Button>
        </div>
    )
}

