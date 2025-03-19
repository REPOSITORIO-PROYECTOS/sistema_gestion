"use client";

import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { useId } from "react";
import { useOptimistic, useState } from "react";

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

    // Estado optimista que se actualiza inmediatamente
    const [optimisticCajaActiva, updateOptimisticCajaActiva] = useOptimistic(
        cajaActiva,
        // Función de actualización que recibe estado actual y acción
        (state, newState: boolean) => newState
    );

    // Función para manejar el cambio del switch
    const handleToggle = async (checked: boolean) => {
        // Actualiza optimistamente antes de la llamada API
        updateOptimisticCajaActiva(checked);

        try {
            // Llamada a la API para actualizar el estado
            const response = await fetch(`/api/cajas/${cajaId}/toggle`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ activa: checked }),
            });

            if (!response.ok) {
                throw new Error("Error al cambiar el estado de la caja");
            }

            // Actualiza el estado real después de la respuesta exitosa
            setCajaActiva(checked);

            // Callback opcional para informar sobre el cambio exitoso
            onToggleSuccess?.(checked);
        } catch (error) {
            // Si falla, revertimos al estado anterior
            console.error("Error al actualizar estado de caja:", error);
            updateOptimisticCajaActiva(!checked);
            setCajaActiva(!checked);
            // Aquí podrías mostrar un mensaje de error
        }
    };

    return (
        <div
            className={`${
                optimisticCajaActiva
                    ? "border-green-500 bg-green-100 text-green-500"
                    : "border-red-500 bg-red-100 text-red-500"
            } relative flex w-full items-start gap-2 rounded-md border p-4 shadow-2xs outline-hidden`}
        >
            <Switch
                id={id}
                className="order-1 h-4 w-6 after:absolute after:inset-0 [&_span]:size-3 [&_span]:data-[state=checked]:translate-x-2 rtl:[&_span]:data-[state=checked]:-translate-x-2"
                aria-describedby={`${id}-description`}
                checked={optimisticCajaActiva}
                onCheckedChange={handleToggle}
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
                </p>
            </div>
        </div>
    );
}
