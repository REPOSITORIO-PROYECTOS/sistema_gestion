"use server"

export async function toggleCajaEstado(activa: boolean) {
    try {
        const response = await fetch(
            `https://sistema-gestion-1.onrender.com/api/caja/estado`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ operacion:"abrir" }),
                // Importante: agregar esta opción si la API está en otro dominio
                cache: "no-store",
            }
        );

        const data = await response.json();

        if (!response.ok) {
            throw new Error("Error al cambiar el estado de la caja");
        }

        return { success: true, data };
    } catch (error) {
        console.error("Error en server action:", error);
        return {
            success: false,
            error: "Error al cambiar el estado de la caja",
        };
    }
}
