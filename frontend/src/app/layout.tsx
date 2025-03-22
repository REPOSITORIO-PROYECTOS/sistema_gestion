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
                className={`${geistSans.variable} ${geistMono.variable} relative antialiased flex flex-col bg-zinc-200 dark:bg-zinc-900 transition-colors text-zinc-900 dark:text-zinc-100 font-sans`}
            >
                <div className="absolute z-0 h-full w-full bg-[radial-gradient(#d4d4d8_1px,transparent_1px)] [background-size:16px_16px] [mask-image:radial-gradient(ellipse_50%_50%_at_50%_50%,#000_70%,transparent_100%)] dark:bg-[radial-gradient(#27272a_1px,transparent_1px)] "></div>
                <ThemeProvider
                    attribute="class"
                    defaultTheme="system"
                    enableSystem
                >
                    <AuthProvider>
                        <NavbarAula />
                        <main className="grow relative">{children}</main>
                    </AuthProvider>
                </ThemeProvider>
                <Toaster />
            </body>
        </html>
    );
}
