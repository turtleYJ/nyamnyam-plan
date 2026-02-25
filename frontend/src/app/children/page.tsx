"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { fetchApi } from "@/lib/api";
import { useChildStore } from "@/stores/childStore";
import type { ChildResponse, ChildCreateRequest } from "@/lib/types";
import AppShell from "@/components/layout/AppShell";
import ChildCard from "@/components/child/ChildCard";
import ChildForm from "@/components/child/ChildForm";
import Button from "@/components/ui/Button";
import Modal from "@/components/ui/Modal";
import Spinner from "@/components/ui/Spinner";
import EmptyState from "@/components/ui/EmptyState";

export default function ChildrenPage() {
  const router = useRouter();
  const { selectedChildId, setSelectedChildId } = useChildStore();
  const [children, setChildren] = useState<ChildResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);

  const loadChildren = useCallback(async () => {
    try {
      const data = await fetchApi<ChildResponse[]>("/api/children");
      setChildren(data);
      if (data.length > 0 && !data.find((c) => c.id === selectedChildId)) {
        setSelectedChildId(data[0].id);
      }
    } catch {
      // handled by fetchApi
    } finally {
      setLoading(false);
    }
  }, [selectedChildId, setSelectedChildId]);

  useEffect(() => {
    loadChildren();
  }, [loadChildren]);

  const handleCreate = async (data: ChildCreateRequest) => {
    await fetchApi<ChildResponse>("/api/children", {
      method: "POST",
      body: data,
    });
    setShowForm(false);
    await loadChildren();
  };

  const handleDelete = async (id: number) => {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    await fetchApi(`/api/children/${id}`, { method: "DELETE" });
    if (selectedChildId === id) setSelectedChildId(null);
    await loadChildren();
  };

  return (
    <AppShell>
      <div className="px-4 pt-6">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold text-gray-900">아이 관리</h1>
          {children.length < 5 && (
            <Button size="sm" onClick={() => setShowForm(true)}>
              + 추가
            </Button>
          )}
        </div>

        {loading ? (
          <Spinner className="mt-20" />
        ) : children.length === 0 ? (
          <EmptyState
            icon="👶"
            title="아이를 등록해주세요"
            description="AI 식단 추천을 받으려면 먼저 아이 정보가 필요해요"
            action={
              <Button onClick={() => setShowForm(true)}>아이 등록하기</Button>
            }
          />
        ) : (
          <>
            <div className="flex flex-col gap-3">
              {children.map((child) => (
                <ChildCard
                  key={child.id}
                  child={child}
                  selected={selectedChildId === child.id}
                  onSelect={() => setSelectedChildId(child.id)}
                  onDelete={() => handleDelete(child.id)}
                />
              ))}
            </div>

            {selectedChildId && (
              <Button
                size="lg"
                className="w-full mt-6"
                onClick={() => router.push("/plans")}
              >
                식단 관리로 이동
              </Button>
            )}
          </>
        )}

        <Modal
          open={showForm}
          onClose={() => setShowForm(false)}
          title="아이 등록"
        >
          <ChildForm
            onSubmit={handleCreate}
            onCancel={() => setShowForm(false)}
          />
        </Modal>
      </div>
    </AppShell>
  );
}
