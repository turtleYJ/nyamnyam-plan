export default function Home() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gradient-to-b from-orange-50 to-white">
      <main className="flex flex-col items-center gap-8 px-6 text-center">
        <div className="text-6xl">🍽️</div>

        <h1 className="text-4xl font-bold text-gray-900">
          냠냠플랜
        </h1>

        <p className="text-lg text-gray-600 max-w-md">
          바쁜 부모를 위한 똑똑한 이유식 플래너
        </p>

        <div className="flex flex-col gap-3 w-full max-w-xs mt-4">
          <button className="w-full py-3 px-6 bg-orange-500 text-white rounded-xl font-medium hover:bg-orange-600 transition-colors">
            시작하기
          </button>
          <button className="w-full py-3 px-6 bg-white text-gray-700 border border-gray-200 rounded-xl font-medium hover:bg-gray-50 transition-colors">
            비회원으로 체험하기
          </button>
        </div>

        <div className="grid grid-cols-3 gap-6 mt-12 text-center">
          <div className="flex flex-col items-center gap-2">
            <span className="text-3xl">📋</span>
            <span className="text-sm text-gray-600">주간 식단</span>
          </div>
          <div className="flex flex-col items-center gap-2">
            <span className="text-3xl">🛒</span>
            <span className="text-sm text-gray-600">장보기 리스트</span>
          </div>
          <div className="flex flex-col items-center gap-2">
            <span className="text-3xl">📊</span>
            <span className="text-sm text-gray-600">영양소 분석</span>
          </div>
        </div>
      </main>
    </div>
  );
}
