import CardSwitch from "@/components/card-switch";
import TableCashItems from "@/components/interfaces/table-cash-items";

export default function Page() {
    return (
        <section className="container flex flex-col gap-12 mx-auto p-16">
            <div className="flex justify-between items-center">
                <h2 className="text-[#1e1e1e] text-2xl font-semibold">
                    PANEL DE CAJA
                </h2>
                <div className="w-full max-w-[300px] flex justify-end">
                    <CardSwitch />
                </div>
            </div>
            <TableCashItems />
        </section>
    );
}
