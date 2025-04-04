import CardSwitch from "@/components/card-switch";
import { DailyBalanceModal } from "@/components/daily-balance-modal";
import TableCashItems from "@/components/interfaces/table-cash-items";

export default function Page() {
    return (
        <section className="container flex flex-col gap-12 mx-auto p-16">
            <div className="flex justify-between items-center">
                <div className="flex flex-col items-start gap-4">
                    <h2 className="text-[#1e1e1e] text-2xl font-semibold">
                        PANEL DE CAJA
                    </h2>
                    <DailyBalanceModal />
                </div>
                <div className="w-full max-w-[300px] flex justify-end">
                    <CardSwitch />
                </div>
            </div>
            <TableCashItems />
        </section>
    );
}
