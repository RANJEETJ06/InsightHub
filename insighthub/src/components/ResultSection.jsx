import React from "react";

const ResultSection = () => {
  return (
    <div className="w-full md:w-1/2 lg:w-3/5">
      <div className="bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl p-6 h-full shadow-inner">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800">Data Insights</h2>
          <div className="flex gap-2">
            <button className="p-3 text-gray-600 hover:text-primary-600 rounded-full hover:bg-white transition-all duration-300 shadow-sm hover:shadow-md transform hover:scale-110">
              <span className="material-symbols-outlined">download</span>
            </button>
            <button className="p-3 text-gray-600 hover:text-primary-600 rounded-full hover:bg-white transition-all duration-300 shadow-sm hover:shadow-md transform hover:scale-110">
              <span className="material-symbols-outlined">share</span>
            </button>
          </div>
        </div>

        <div className="mb-6">
          <div className="flex gap-3 mb-4">
            <button className="px-5 py-2 bg-gradient-to-r from-primary-500 to-primary-600 text-white rounded-full hover:from-primary-600 hover:to-primary-700 transition-all duration-300 shadow-md hover:shadow-lg transform hover:scale-105">
              All Insights
            </button>
            <button className="px-5 py-2 bg-white text-gray-700 rounded-full hover:bg-gray-100 transition-all duration-300 shadow hover:shadow-md transform hover:scale-105">
              PDF Format
            </button>
          </div>
        </div>

        <div className="space-y-6 overflow-y-auto max-h-[600px] pr-2 custom-scrollbar">
          <div className="bg-white p-5 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 border border-gray-100 transform hover:-translate-y-1">
            <div className="flex justify-between items-start mb-3">
              <h3 className="font-semibold text-lg">
                Revenue by Product Category
              </h3>
              <div className="flex gap-1">
                <button
                  className="p-2 text-gray-400 hover:text-primary-500 rounded-full hover:bg-gray-50 transition-all duration-300 transform hover:scale-110"
                  title="Download PDF"
                >
                  <span className="material-symbols-outlined text-sm">
                    file_download
                  </span>
                </button>
              </div>
            </div>
            <div className="h-64 w-full bg-gradient-to-br from-gray-50 to-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
              <span className="material-symbols-outlined text-6xl text-primary-300 animate-pulse">
                bar_chart
              </span>
            </div>
            <p className="mt-3 text-sm text-gray-600">
              This chart shows the distribution of revenue across your product
              categories, with electronics leading at 45%.
            </p>
          </div>

          <div className="bg-white p-5 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 border border-gray-100 transform hover:-translate-y-1">
            <div className="flex justify-between items-start mb-3">
              <h3 className="font-semibold text-lg">Monthly Sales Trend</h3>
              <div className="flex gap-1">
                <button
                  className="p-2 text-primary-500 rounded-full hover:bg-gray-50 transition-all duration-300 transform hover:scale-110"
                  title="Download PDF"
                >
                  <span className="material-symbols-outlined text-sm">
                    file_download
                  </span>
                </button>
              </div>
            </div>
            <div className="h-64 w-full bg-gradient-to-br from-gray-50 to-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
              <span className="material-symbols-outlined text-6xl text-primary-300 animate-pulse">
                landscape
              </span>
            </div>
            <p className="mt-3 text-sm text-gray-600">
              Your sales show a strong seasonal pattern with peaks in November
              and December, suggesting holiday shopping effects.
            </p>
          </div>

          <div className="bg-white p-5 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 border border-gray-100 transform hover:-translate-y-1">
            <div className="flex justify-between items-start mb-3">
              <h3 className="font-semibold text-lg">Customer Demographics</h3>
              <div className="flex gap-1">
                <button
                  className="p-2 text-gray-400 hover:text-primary-500 rounded-full hover:bg-gray-50 transition-all duration-300 transform hover:scale-110"
                  title="Download PDF"
                >
                  <span className="material-symbols-outlined text-sm">
                    file_download
                  </span>
                </button>
              </div>
            </div>
            <div className="h-64 w-full bg-gradient-to-br from-gray-50 to-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
              <span className="material-symbols-outlined text-6xl text-primary-300 animate-pulse">
                pie_chart
              </span>
            </div>
            <p className="mt-3 text-sm text-gray-600">
              Customer age distribution shows 65% of customers are between 25-45
              years old, with 58% identifying as female.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ResultSection;
