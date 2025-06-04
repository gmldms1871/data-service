// @ts-check

import globals from "globals";
import pluginJs from "@eslint/js";
import tseslint from "typescript-eslint";
import eslintPluginReact from "eslint-plugin-react";
import eslintPluginPrettierRecommended from "eslint-plugin-prettier/recommended";

export default [
  {
    ignores: [
      "node_modules/",
      "backend/",
      "build/",
      "dist/",
      ".vscode/",
      "frontend/build/",
    ],
  },
  {
    // Global language options and plugins
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.node,
      },
    },
  },
  pluginJs.configs.recommended,
  ...tseslint.configs.recommended, // If using TypeScript
  {
    // React specific configurations
    files: ["frontend/src/**/*.{js,jsx,ts,tsx}"],
    plugins: {
      react: eslintPluginReact,
    },
    languageOptions: {
      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },
      },
    },
    rules: {
      "react/jsx-uses-react": "warn",
      "react/react-in-jsx-scope": "warn",
      // Add other React rules here
    },
    settings: {
      react: {
        version: "detect",
      },
    },
  },
  eslintPluginPrettierRecommended, // This should be last to override other formatting rules
];
