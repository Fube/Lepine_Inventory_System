import Nav from "../../components/Nav";
import ItemBase from "../../components/Item";

export default function CreateItem() {
    return (
        <div className="flex flex-col h-screen">
            <div className="flex-shrink-0 flex-grow-0">
                <Nav />
            </div>
            <div className="flex-grow flex justify-center items-center">
                <div className="w-full">
                    <ItemBase editable />
                </div>
            </div>
        </div>
    );
}
