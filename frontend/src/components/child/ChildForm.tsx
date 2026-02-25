"use client";

import { useState } from "react";
import type { ChildCreateRequest } from "@/lib/types";
import Button from "@/components/ui/Button";
import Input from "@/components/ui/Input";

interface ChildFormProps {
  onSubmit: (data: ChildCreateRequest) => Promise<void>;
  onCancel: () => void;
}

const ALLERGY_OPTIONS = [
  "우유",
  "계란",
  "밀",
  "대두",
  "땅콩",
  "견과류",
  "새우",
  "게",
  "생선",
  "복숭아",
  "토마토",
];

export default function ChildForm({ onSubmit, onCancel }: ChildFormProps) {
  const [name, setName] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [gender, setGender] = useState<"MALE" | "FEMALE" | null>(null);
  const [allergies, setAllergies] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  const toggleAllergy = (allergy: string) => {
    setAllergies((prev) =>
      prev.includes(allergy)
        ? prev.filter((a) => a !== allergy)
        : [...prev, allergy]
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !birthDate) return;

    setLoading(true);
    try {
      await onSubmit({
        name: name.trim(),
        birth_date: birthDate,
        gender,
        allergies,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4">
      <Input
        label="이름"
        placeholder="아이 이름"
        value={name}
        onChange={(e) => setName(e.target.value)}
        required
        maxLength={30}
      />

      <Input
        label="생년월일"
        type="date"
        value={birthDate}
        onChange={(e) => setBirthDate(e.target.value)}
        required
      />

      <div>
        <label className="text-sm font-medium text-gray-700 block mb-2">
          성별
        </label>
        <div className="flex gap-2">
          {(
            [
              { value: "MALE", label: "남아 👦" },
              { value: "FEMALE", label: "여아 👧" },
            ] as const
          ).map((opt) => (
            <button
              key={opt.value}
              type="button"
              onClick={() =>
                setGender(gender === opt.value ? null : opt.value)
              }
              className={`flex-1 py-2 rounded-xl border text-sm transition-colors ${
                gender === opt.value
                  ? "border-brand bg-brand-50 text-brand-600 font-medium"
                  : "border-gray-200 text-gray-600 hover:bg-gray-50"
              }`}
            >
              {opt.label}
            </button>
          ))}
        </div>
      </div>

      <div>
        <label className="text-sm font-medium text-gray-700 block mb-2">
          알레르기 (선택)
        </label>
        <div className="flex flex-wrap gap-2">
          {ALLERGY_OPTIONS.map((allergy) => (
            <button
              key={allergy}
              type="button"
              onClick={() => toggleAllergy(allergy)}
              className={`px-3 py-1.5 rounded-full text-sm border transition-colors ${
                allergies.includes(allergy)
                  ? "border-red-300 bg-red-50 text-red-600"
                  : "border-gray-200 text-gray-600 hover:bg-gray-50"
              }`}
            >
              {allergy}
            </button>
          ))}
        </div>
      </div>

      <div className="flex gap-2 mt-2">
        <Button
          type="button"
          variant="secondary"
          onClick={onCancel}
          className="flex-1"
        >
          취소
        </Button>
        <Button type="submit" loading={loading} className="flex-1">
          등록하기
        </Button>
      </div>
    </form>
  );
}
