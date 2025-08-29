import React from "react";

const UploadSection = () => {
  return (
    <div className="w-full md:w-1/2 lg:w-2/5">
      <div className="bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl p-6 border-2 border-dashed border-gray-300 hover:border-primary-500 transition-all duration-300 transform hover:-translate-y-1">
        <h2 className="text-2xl font-bold mb-4 text-gray-800">
          Upload Your Data
        </h2>
        <p className="text-gray-600 mb-6">
          Upload your CSV file to generate insightful visualizations and reports
        </p>

        <div className="mb-6">
          <label className="flex flex-col items-center justify-center h-48 rounded-xl border-2 border-dashed border-gray-300 cursor-pointer bg-white hover:bg-gray-50 transition-all duration-300 group">
            <div className="flex flex-col items-center justify-center pt-5 pb-6 transition-all duration-300 transform group-hover:scale-105">
              <span className="material-symbols-outlined text-6xl text-primary-500 mb-3 animate-pulse">
                upload_file
              </span>
              <p className="mb-2 text-sm text-gray-600">
                <span className="font-semibold">Click to upload</span> or drag
                and drop
              </p>
              <p className="text-xs text-gray-500">CSV files only</p>
            </div>
            <input type="file" className="hidden" accept=".csv" />
          </label>
        </div>

        <div className="flex justify-center">
          <button className="px-8 py-3 bg-gradient-to-r from-primary-600 to-primary-700 text-white font-medium rounded-lg hover:from-primary-700 hover:to-primary-800 focus:ring-4 focus:ring-primary-300 transition-all duration-300 flex items-center gap-2 transform hover:scale-105 shadow-lg hover:shadow-xl">
            <span className="material-symbols-outlined">analytics</span>
            Generate Insights
          </button>
        </div>
      </div>

      <div className="mt-6 p-6 bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl border border-blue-200 shadow-sm transition-all duration-300 hover:shadow-md">
        <h3 className="text-lg font-bold flex items-center gap-2 mb-3 text-blue-800">
          <span className="material-symbols-outlined text-blue-600">
            tips_and_updates
          </span>
          How It Works
        </h3>
        <ol className="list-decimal list-inside text-gray-700 space-y-3 text-sm pl-2">
          <li className="transition-all duration-200 hover:text-blue-700 hover:translate-x-1">
            Upload your CSV data file
          </li>
          <li className="transition-all duration-200 hover:text-blue-700 hover:translate-x-1">
            Our AI (powered by Gemini) analyzes your data
          </li>
          <li className="transition-all duration-200 hover:text-blue-700 hover:translate-x-1">
            View automatically generated insights and visualizations
          </li>
          <li className="transition-all duration-200 hover:text-blue-700 hover:translate-x-1">
            Download your report in PDF format
          </li>
          <li className="transition-all duration-200 hover:text-blue-700 hover:translate-x-1">
            Share insights with your team
          </li>
        </ol>
      </div>
    </div>
  );
};

export default UploadSection;
