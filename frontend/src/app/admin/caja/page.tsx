"use client";
import CardSwitch from "@/components/card-switch";
import { DailyBalanceModal } from "@/components/daily-balance-modal";
import TableCashItems from "@/components/interfaces/table-cash-items";
import { useState } from "react";

export default function Page() {
    const [cajaActivaGeneral, setCajaActivaGeneral] = useState(false);
    return (
        <section className="container flex flex-col gap-12 mx-auto p-16">
            <div className="flex justify-between items-center">
                <div className="flex flex-col items-start gap-4">
                    <h2 className="text-primary text-2xl font-semibold">
                        PANEL DE CAJA
                    </h2>
                    <DailyBalanceModal />
                </div>
                <div className="w-full max-w-[300px] flex justify-end">
                    <CardSwitch setCajaActivaGeneral={setCajaActivaGeneral} />
                </div>
            </div>
            <TableCashItems cajaActivaGeneral={cajaActivaGeneral} />
        </section>
    );
}
