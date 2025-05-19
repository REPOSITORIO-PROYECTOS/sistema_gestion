// context/DebtContext.tsx
'use client';

import React, { createContext, useContext, useEffect, useState } from "react";
import axios from "axios";

// Tipo de cuota
export interface Cuota {
    id: string;
    studentId: string;
    studentName: string;
    curseId: string;
    curseName: string;
    paymentAmount: number;
    paidAmount: number;
    hasDebt: boolean;
    isPaid: boolean;
  }

// Tipo del contexto
interface DebtContextType {
  cuotas: Cuota[];
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

// Contexto inicial
const CoutasAdeudadasContext = createContext<DebtContextType | undefined>(undefined);

// Hook para consumir el contexto
export const useDebt = (): DebtContextType => {
  const context = useContext(CoutasAdeudadasContext);
  if (!context) {
    throw new Error("useDebt debe usarse dentro de un DebtProvider");
  }
  return context;
};

// Provider
export const CuotasAdeudadasProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [cuotas, setCuotas] = useState<Cuota[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchCuotas = async () => {
    setLoading(true);
    setError(null);
    const userString = localStorage.getItem("auth-storage");
    const user = userString ? JSON.parse(userString) : null;
    try {
      const response = await axios.get("https://instituto.sistemataup.online//api/pagos/con-deuda?hasDebt=true&page=0&size=1000", {
        headers: {
          Authorization: `Bearer ${user.state.user.token}`,
        },
      });
      setCuotas(response.data.content.filter(((cuota:any) => cuota.paymentAmount != cuota.paidAmount)));
    } catch (err) {
      setError("Error al obtener las cuotas adeudadas");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCuotas();
  }, []);

  return (
    <CoutasAdeudadasContext.Provider value={{ cuotas, loading, error, refetch: fetchCuotas }}>
      {children}
    </CoutasAdeudadasContext.Provider>
  );
};
