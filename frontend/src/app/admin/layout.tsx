import type { Metadata } from "next";

import "@/styles/globals.css";
import { CuotasAdeudadasProvider } from "@/context/coutasAdeudadasContext";

export const metadata: Metadata = {
    title: "Sistema gestion - Admin",
    description: "Sistema de gestion de ventas",
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return <>
        <CuotasAdeudadasProvider>
            {children}
        </CuotasAdeudadasProvider>
    </>;
}
