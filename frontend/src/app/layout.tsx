import { AuthProvider } from "@/components/providers/auth-provider";
import { Geist, Geist_Mono } from "next/font/google";
import { Navbar } from "@/components/navbar";
import type { Metadata } from "next";
import { Toaster } from "sonner";

import "@/styles/globals.css";
import { ThemeProvider } from "@/components/providers/theme-provider";
import NavbarAula from "@/components/navbar-aula";

const geistSans = Geist({
    variable: "--font-geist-sans",
    subsets: ["latin"],
});

const geistMono = Geist_Mono({
    variable: "--font-geist-mono",
    subsets: ["latin"],
});

export const metadata: Metadata = {
    title: "Sistema gestion - Aula Virtual",
    description: "Aula Virtual",
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="es" suppressHydrationWarning>
            <body
                className={`${geistSans.variable} ${geistMono.variable} relative antialiased flex flex-col min-h-screen`}
            >
                <div className="absolute z-0 h-full w-full bg-[radial-gradient(#d4d4d8_1px,transparent_1px)] [background-size:16px_16px] [mask-image:radial-gradient(ellipse_50%_50%_at_50%_50%,#000_70%,transparent_100%)] dark:bg-[radial-gradient(#27272a_1px,transparent_1px)] "></div>
                <ThemeProvider
                    attribute="class"
                    defaultTheme="system"
                    enableSystem
                    disableTransitionOnChange
                >
                    <AuthProvider>
                        <main className="grow relative z-10">
                            <NavbarAula />
                            {children}
                        </main>
                    </AuthProvider>
                </ThemeProvider>
                <Toaster />
            </body>
        </html>
    );
}
