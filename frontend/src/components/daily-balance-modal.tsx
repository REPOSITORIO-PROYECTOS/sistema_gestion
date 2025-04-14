"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "@/components/ui/dialog";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
    CalendarIcon,
    DollarSign,
    Search,
    TrendingDown,
    TrendingUp,
} from "lucide-react";
import { format } from "date-fns";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Label } from "@/components/ui/label";

interface DailyBalance {
    id: string;
    startDate: string;
    endDate: string;
    totalIncome: number;
    totalExpense: number;
    createdBy: string;
    closedBy: string;
    isClosed: boolean;
}

interface BalanceData {
    dailyBalances: DailyBalance[];
    totalIncome: number;
    totalExpense: number;
}

export function DailyBalanceModal() {
    const [data, setData] = useState<BalanceData | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [open, setOpen] = useState(false);
    const [month, setMonth] = useState<string>("");
    const [year, setYear] = useState<string>("");

    const fetchData = async () => {
        if (!month || !year) {
            setError("Por favor seleccione mes y año");
            return;
        }

        setLoading(true);
        setError(null);

        try {
            // Using the provided endpoint with query parameters
            const response = await fetch(
                `https://sistema-gestion-1.onrender.com/api/caja/balance-mensual?year=${year}&month=${month}`
            );

            if (!response.ok) {
                throw new Error("Failed to fetch data");
            }

            const result = await response.json();
            setData(result);
        } catch (err) {
            setError(err instanceof Error ? err.message : "An error occurred");
        } finally {
            setLoading(false);
        }
    };

    const handleOpenChange = (newOpen: boolean) => {
        setOpen(newOpen);
        if (!newOpen) {
            // Reset data when closing the modal
            setData(null);
        }
    };

    const formatDate = (dateString: string) => {
        try {
            // Parse the date string (assuming format DD-MM-YYYYTHH:MM)
            const [datePart, timePart] = dateString.split("T");
            const [day, month, year] = datePart.split("-");
            const date = new Date(`${year}-${month}-${day}T${timePart}:00`);

            return format(date, "PPP p"); // Format: "Apr 29, 2021 12:00 PM"
        } catch (e) {
            return dateString; // Return original if parsing fails
        }
    };

    // Generate years for the dropdown (current year and 5 years back)
    const currentYear = new Date().getFullYear();
    const years = Array.from({ length: 6 }, (_, i) => currentYear - i);

    // Months for the dropdown
    const months = [
        { value: "1", label: "Enero" },
        { value: "2", label: "Febrero" },
        { value: "3", label: "Marzo" },
        { value: "4", label: "Abril" },
        { value: "5", label: "Mayo" },
        { value: "6", label: "Junio" },
        { value: "7", label: "Julio" },
        { value: "8", label: "Agosto" },
        { value: "9", label: "Septiembre" },
        { value: "10", label: "Octubre" },
        { value: "11", label: "Noviembre" },
        { value: "12", label: "Diciembre" },
    ];

    return (
        <Dialog open={open} onOpenChange={handleOpenChange}>
            <DialogTrigger asChild>
                <Button>Ver Balance Mensual</Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[600px]">
                <DialogHeader>
                    <DialogTitle>Balance Mensual</DialogTitle>
                    <DialogDescription>
                        Seleccione mes y año para ver el balance
                    </DialogDescription>
                </DialogHeader>

                <div className="grid grid-cols-3 gap-4 py-4">
                    <div className="grid gap-2">
                        <Label htmlFor="month">Mes</Label>
                        <Select value={month} onValueChange={setMonth}>
                            <SelectTrigger id="month">
                                <SelectValue placeholder="Seleccionar mes" />
                            </SelectTrigger>
                            <SelectContent>
                                {months.map((m) => (
                                    <SelectItem key={m.value} value={m.value}>
                                        {m.label}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="grid gap-2">
                        <Label htmlFor="year">Año</Label>
                        <Select value={year} onValueChange={setYear}>
                            <SelectTrigger id="year">
                                <SelectValue placeholder="Seleccionar año" />
                            </SelectTrigger>
                            <SelectContent>
                                {years.map((y) => (
                                    <SelectItem key={y} value={y.toString()}>
                                        {y}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="flex items-end">
                        <Button
                            onClick={fetchData}
                            className="w-full"
                            disabled={loading}
                        >
                            {loading ? (
                                "Buscando..."
                            ) : (
                                <>
                                    <Search className="h-4 w-4 mr-2" />
                                    Buscar
                                </>
                            )}
                        </Button>
                    </div>
                </div>

                {error && (
                    <div className="bg-red-50 text-red-500 p-4 rounded-md">
                        Error: {error}
                    </div>
                )}

                {data && (
                    <div className="space-y-6 max-h-96 overflow-y-scroll">
                        <div className="grid grid-cols-2 gap-4">
                            <Card>
                                <CardHeader className="pb-2">
                                    <CardTitle className="text-sm font-medium flex items-center">
                                        <TrendingUp className="h-4 w-4 mr-2 text-green-500" />
                                        Ingresos Totales
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-2xl font-bold">
                                        ${data.totalIncome.toFixed(2)}
                                    </p>
                                </CardContent>
                            </Card>

                            <Card>
                                <CardHeader className="pb-2">
                                    <CardTitle className="text-sm font-medium flex items-center">
                                        <TrendingDown className="h-4 w-4 mr-2 text-red-500" />
                                        Gastos Totales
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <p className="text-2xl font-bold">
                                        ${data.totalExpense.toFixed(2)}
                                    </p>
                                </CardContent>
                            </Card>
                        </div>

                        <div>
                            <h3 className="text-lg font-medium mb-3">
                                Balances Diarios
                            </h3>
                            {data.dailyBalances.length > 0 ? (
                                data.dailyBalances.map((balance) => (
                                    <Card key={balance.id} className="mb-4">
                                        <CardContent className="pt-6">
                                            <div className="grid gap-2">
                                                <div className="flex items-center justify-between">
                                                    <div className="flex items-center text-sm text-muted-foreground">
                                                        <CalendarIcon className="h-4 w-4 mr-1" />
                                                        <span>
                                                            Desde:{" "}
                                                            {formatDate(
                                                                balance.startDate
                                                            )}
                                                        </span>
                                                    </div>
                                                    <div className="flex items-center text-sm text-muted-foreground">
                                                        <CalendarIcon className="h-4 w-4 mr-1" />
                                                        <span>
                                                            Hasta:{" "}
                                                            {formatDate(
                                                                balance.endDate
                                                            )}
                                                        </span>
                                                    </div>
                                                </div>

                                                <div className="grid grid-cols-2 gap-4 mt-2">
                                                    <div className="flex items-center">
                                                        <DollarSign className="h-4 w-4 mr-1 text-green-500" />
                                                        <span className="font-medium">
                                                            Ingresos:
                                                        </span>
                                                        <span className="ml-1">
                                                            $
                                                            {balance.totalIncome.toFixed(
                                                                2
                                                            )}
                                                        </span>
                                                    </div>
                                                    <div className="flex items-center">
                                                        <DollarSign className="h-4 w-4 mr-1 text-red-500" />
                                                        <span className="font-medium">
                                                            Gastos:
                                                        </span>
                                                        <span className="ml-1">
                                                            $
                                                            {balance.totalExpense.toFixed(
                                                                2
                                                            )}
                                                        </span>
                                                    </div>
                                                </div>

                                                <div className="flex justify-between mt-2 text-sm">
                                                    <div>
                                                        <span className="text-muted-foreground">
                                                            Creado por:
                                                        </span>
                                                        <span className="ml-1 font-medium">
                                                            {balance.createdBy}
                                                        </span>
                                                    </div>
                                                    <div>
                                                        <span className="text-muted-foreground">
                                                            Cerrado por:
                                                        </span>
                                                        <span className="ml-1 font-medium">
                                                            {balance.closedBy}
                                                        </span>
                                                    </div>
                                                    <div>
                                                        <span className="text-muted-foreground">
                                                            Estado:
                                                        </span>
                                                        <span
                                                            className={`ml-1 font-medium ${
                                                                balance.isClosed
                                                                    ? "text-red-500"
                                                                    : "text-green-500"
                                                            }`}
                                                        >
                                                            {balance.isClosed
                                                                ? "Cerrado"
                                                                : "Abierto"}
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                        </CardContent>
                                    </Card>
                                ))
                            ) : (
                                <p className="text-center py-4 text-muted-foreground">
                                    No hay balances diarios para el período
                                    seleccionado
                                </p>
                            )}
                        </div>
                    </div>
                )}
            </DialogContent>
        </Dialog>
    );
}
