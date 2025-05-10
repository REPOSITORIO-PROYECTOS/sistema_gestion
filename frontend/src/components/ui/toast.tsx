import React from "react";
import { Toaster, toast } from "sonner";

// Función para mostrar el toast
export const toastMessage = ({ variant, title, description }: any) => {
    const commonStyles = {
        borderRadius: "8px",
        padding: "16px",
        fontWeight: "600",
    };

    const toastStyles: any = {
        destructive: {
            backgroundColor: "#e74c3c", // Rojo para destructivo
            color: "white",
        },
        success: {
            backgroundColor: "#2ecc71", // Verde para éxito
            color: "white",
        },
        info: {
            backgroundColor: "#3498db", // Azul para información
            color: "white",
        },
    };

    // Elige el estilo según el 'variant'
    const variantStyles = toastStyles[variant] || toastStyles.info;

    toast.custom((t) => (
        <div
            className={`toast ${t.visible ? "fade-in" : "fade-out"}`}
            style={{
                ...commonStyles,
                ...variantStyles,
            }}
        >
            <div className="font-semibold">{title}</div>
            <div className="text-sm">{description}</div>
        </div>
    ));
};

function App() {
    // Simulando un error
    const simulateError = () => {
        const error = new Error("Hubo un error al cargar los detalles.");
        toastMessage({
            variant: "destructive",
            title: "Error al cargar detalles",
            description: error.message,
        });
    };

    return (
        <div className="App">
            <h1 className="text-3xl font-semibold text-center mt-10">
                Aplicación con Toasts
            </h1>
            <div className="flex justify-center mt-5">
                <button
                    onClick={simulateError}
                    className="bg-blue-500 text-white p-3 rounded-md"
                >
                    Simular Error
                </button>
            </div>

            {/* Toast global */}
            <Toaster />
        </div>
    );
}
