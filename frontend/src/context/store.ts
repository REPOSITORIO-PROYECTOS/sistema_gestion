import { create } from "zustand";
import { persist } from "zustand/middleware";

// Definición de tipos para la respuesta del API
export interface AuthResponse {
    token: string;
    username: string;
    name: string;
    role: string[];
}

// Tipo de usuario simplificado para mantener compatibilidad
export type User = {
    token: string;
    username: string;
    name: string;
    role: "ROLE_ADMIN" | "ROLE_STUDENT" | "ROLE_USER" | "ROLE_TEACHER" | "ROLE_PARENT" | string[];
} | null;

// Definición del estado de autenticación
interface AuthState {
    user: User;
    isAuthenticated: boolean;
    isLoading: boolean;
    error: string | null;
    login: (
        email: string,
        password: string,
        role: "ROLE_ADMIN" | "ROLE_STUDENT"
    ) => Promise<boolean>;
    logout: () => void;
    clearError: () => void;
}

// Creación del store con persistencia
export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            user: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,

            // Función de login
            login: async (
                email: string,
                password: string,
                role: "ROLE_ADMIN" | "ROLE_STUDENT"
            ) => {
                set({ isLoading: true, error: null });

                try {
                    // Llamada al endpoint de autenticación
                    const response = await fetch(
                        "https://instituto.sistemataup.online/api/auth/login",
                        {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({ username: email, password }),
                        }
                    );

                    if (!response.ok) {
                        const errorData = await response.json();
                        throw new Error(
                            errorData.message || "Error en la autenticación"
                        );
                    }

                    const userData: AuthResponse = await response.json();

                    // Actualizar el estado con los datos del usuario
                    set({
                        user: {
                            ...userData,
                            // Asegurar que role sea compatible con el tipo esperado
                            role: Array.isArray(userData.role)
                                ? (userData.role[0] as "ROLE_ADMIN" | "ROLE_STUDENT" | "ROLE_USER" | "ROLE_TEACHER" | "ROLE_PARENT")
                                : userData.role,
                        },
                        isAuthenticated: true,
                        isLoading: false,
                    });

                    return true;
                } catch (error) {
                    set({
                        isLoading: false,
                        error:
                            error instanceof Error
                                ? error.message
                                : "Error desconocido",
                    });
                    return false;
                }
            },

            // Función de logout
            logout: () => {
                set({
                    user: null,
                    isAuthenticated: false,
                    error: null,
                });
                // Limpiar sessionStorage para mantener compatibilidad
                sessionStorage.removeItem("user");
            },

            // Limpiar errores
            clearError: () => set({ error: null }),
        }),
        {
            name: "auth-storage", // Nombre para localStorage
            partialize: (state) => ({
                user: state.user,
                isAuthenticated: state.isAuthenticated,
            }),
        }
    )
);
