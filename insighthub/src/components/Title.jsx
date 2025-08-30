import React from "react";

const Title = () => {
  return (
    <header className="mb-12 transition-all duration-500 hover:transform hover:scale-105">
      <h1 className="text-4xl md:text-5xl lg:text-6xl font-extrabold text-center bg-clip-text text-transparent bg-gradient-to-r from-primary-600 to-purple-600">
        Insight Generator
      </h1>
      <p className="text-center text-gray-600 mt-3 text-lg">
        Upload your CSV file and get AI-powered visual insights instantly
      </p>
    </header>
  );
};

export default Title;
