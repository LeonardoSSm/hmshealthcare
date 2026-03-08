/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          50: "#eefaf7",
          500: "#0f766e",
          700: "#115e59",
          900: "#0b3f3b"
        }
      }
    }
  },
  plugins: []
};
