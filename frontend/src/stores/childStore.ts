import { create } from "zustand";
import { persist } from "zustand/middleware";

interface ChildState {
  selectedChildId: number | null;
  setSelectedChildId: (id: number | null) => void;
}

export const useChildStore = create<ChildState>()(
  persist(
    (set) => ({
      selectedChildId: null,
      setSelectedChildId: (id) => set({ selectedChildId: id }),
    }),
    { name: "nyamnyam-child" }
  )
);
