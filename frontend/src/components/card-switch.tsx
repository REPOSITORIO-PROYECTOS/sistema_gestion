"use client";

import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { cn } from "@/lib/utils";
import { useId } from "react";
import { useOptimistic, useState, startTransition } from "react";
import { toast } from "sonner";

interface CardSwitchProps {
    initialState?: boolean;
    cajaId?: string;
    sublabel?: string;
    description?: string;
    onToggleSuccess?: (newState: boolean) => void;
}

export default function CardSwitch({
    initialState = false,
    cajaId,
    sublabel = "(Sublabel)",
    description = "A short description goes here.",
    onToggleSuccess,
}: CardSwitchProps) {
    const id = useId();
    const [cajaActiva, setCajaActiva] = useState(initialState);
    const [isLoading, setIsLoading] = useState(false);

    // Estado optimista que se actualiza inmediatamente
    const [optimisticCajaActiva, updateOptimisticCajaActiva] = useOptimistic(
        cajaActiva,
        (_, newValue: boolean) => newValue
    );

    // Función para manejar el cambio del switch
    const handleToggle = async (checked: boolean) => {
        // Prevenir múltiples clics mientras está cargando
        if (isLoading) return;

        setIsLoading(true);

        // Actualización optimista inmediata
        startTransition(() => {
            updateOptimisticCajaActiva(checked);
        });

        try {
            console.log("Enviando petición a la API con estado:", checked);
            // Llamada a la API para actualizar el estado
            const response = await fetch(
                `https://sistema-gestion-1.onrender.com/api/caja/cerrar`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ activa: checked }),
                }
            );

            const data = await response.json();
            console.log("Respuesta de la API:", data);

            if (!response.ok) {
                throw new Error("Error al cambiar el estado de la caja");
            }

            // Actualiza el estado real después de la respuesta exitosa
            setCajaActiva(checked);

            // Callback opcional para informar sobre el cambio exitoso
            if (onToggleSuccess) {
                onToggleSuccess(checked);
            }

            toast.success(
                `Caja ${checked ? "activada" : "desactivada"} correctamente`
            );
        } catch (error) {
            console.error("Error al actualizar estado de caja:", error);

            // Revertir la actualización optimista si hay error
            startTransition(() => {
                updateOptimisticCajaActiva(!checked);
            });

            setCajaActiva(!checked);

            toast.error(
                "Error al cambiar el estado de la caja. Por favor, inténtalo de nuevo."
            );
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div
            className={cn(
                optimisticCajaActiva
                    ? "border-green-500 bg-green-100 text-green-500"
                    : "border-red-500 bg-red-100 text-red-500",
                "relative flex w-full items-start gap-2 rounded-md border p-4 shadow-2xs outline-hidden"
            )}
        >
            <Switch
                id={id}
                className="order-1 h-4 w-6 after:absolute after:inset-0 [&_span]:size-3 [&_span]:data-[state=checked]:translate-x-2 rtl:[&_span]:data-[state=checked]:-translate-x-2"
                aria-describedby={`${id}-description`}
                checked={optimisticCajaActiva}
                onCheckedChange={handleToggle}
                disabled={isLoading}
            />
            <div className="grid grow gap-2">
                <Label htmlFor={id}>
                    <p>
                        Caja{" "}
                        <span className="text-muted-foreground text-xs leading-[inherit] font-normal">
                            {sublabel}
                        </span>
                    </p>
                </Label>
                <p id={`${id}-description`} className="text-xs">
                    {description}
                </p>
                <p className="text-xs font-medium">
                    Estado: {optimisticCajaActiva ? "Activa" : "Inactiva"}
                    {isLoading && " (actualizando...)"}
                </p>
            </div>
        </div>
    );
}
