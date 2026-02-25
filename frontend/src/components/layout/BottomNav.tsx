"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const tabs = [
  { href: "/children", label: "아이", icon: "👶" },
  { href: "/plans", label: "식단", icon: "📋" },
  { href: "/recipes", label: "레시피", icon: "🍳" },
];

export default function BottomNav() {
  const pathname = usePathname();

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 pb-[env(safe-area-inset-bottom)]">
      <div className="flex justify-around max-w-md mx-auto">
        {tabs.map((tab) => {
          const active = pathname.startsWith(tab.href);
          return (
            <Link
              key={tab.href}
              href={tab.href}
              className={`flex flex-col items-center py-2 px-4 text-xs transition-colors ${
                active ? "text-brand font-semibold" : "text-gray-400"
              }`}
            >
              <span className="text-xl mb-0.5">{tab.icon}</span>
              {tab.label}
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
