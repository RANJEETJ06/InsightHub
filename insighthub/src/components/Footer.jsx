import React from "react";

const Footer = () => {
  // Custom alert handler
  const handleAlert = (type) => {
    if (type === "privacy") {
      alert(
        "Privacy Policy: Your data is secure and will remain confidential. If not used by you within 30 days, your data will be deleted permanently."
      );
    } else if (type === "terms") {
      alert("Terms of Service: Your data will be safe and used responsibly.");
    }
  };

  return (
    <footer className="mt-16 text-center text-gray-500 text-sm py-6">
      <p className="text-base">
        © 2023 Insight Generator. Powered by Gemini AI.
      </p>
      <div className="flex justify-center gap-6 mt-3">
        <button
          onClick={() => handleAlert("privacy")}
          className="hover:text-primary-600 transition-all duration-300 transform hover:scale-105"
        >
          Privacy Policy
        </button>
        <button
          onClick={() => handleAlert("terms")}
          className="hover:text-primary-600 transition-all duration-300 transform hover:scale-105"
        >
          Terms of Service
        </button>
        <a
          href="https://github.com/RANJEETJ06"
          target="_blank"
          rel="noopener noreferrer"
          className="hover:text-primary-600 transition-all duration-300 transform hover:scale-105"
        >
          Contact
        </a>
      </div>
    </footer>
  );
};

export default Footer;
