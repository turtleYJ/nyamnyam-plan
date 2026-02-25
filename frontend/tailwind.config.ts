import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "var(--background)",
        foreground: "var(--foreground)",
        brand: {
          DEFAULT: "#FF6B35",
          50: "#FFF3ED",
          100: "#FFE4D4",
          200: "#FFC5A8",
          300: "#FF9E70",
          400: "#FF6B35",
          500: "#F04E15",
          600: "#D1360A",
          700: "#AD280C",
          800: "#8A2112",
          900: "#701E12",
        },
      },
    },
  },
  plugins: [],
};
export default config;
