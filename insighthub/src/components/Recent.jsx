import React from "react";

const Recent = () => {
  return (
    <div className="mt-10 bg-white rounded-2xl shadow-xl overflow-hidden p-8 transform transition-all duration-500 hover:shadow-2xl hover:-translate-y-1">
      <h2 className="text-2xl font-bold mb-6 text-gray-800">
        Recently Generated Reports
      </h2>
      <div className="overflow-x-auto custom-scrollbar">
        <table className="w-full text-sm text-left">
          <thead className="text-xs uppercase bg-gradient-to-r from-gray-50 to-gray-100">
            <tr>
              <th className="px-6 py-4 font-semibold">File Name</th>
              <th className="px-6 py-4 font-semibold">Date</th>
              <th className="px-6 py-4 font-semibold">Size</th>
              <th className="px-6 py-4 font-semibold">Format</th>
              <th className="px-6 py-4 font-semibold">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr className="border-b hover:bg-gray-50 transition-all duration-300">
              <td className="px-6 py-4 font-medium">sales_data_2023.csv</td>
              <td className="px-6 py-4">2023-12-15</td>
              <td className="px-6 py-4">1.2 MB</td>
              <td className="px-6 py-4">PDF</td>
              <td className="px-6 py-4">
                <div className="flex gap-3">
                  <button
                    className="p-2 text-gray-600 hover:text-primary-600 rounded-full hover:bg-gray-100 transition-all duration-300 transform hover:scale-110"
                    title="View"
                  >
                    <span className="material-symbols-outlined text-sm">
                      visibility
                    </span>
                  </button>
                  <button
                    className="p-2 text-gray-600 hover:text-primary-600 rounded-full hover:bg-gray-100 transition-all duration-300 transform hover:scale-110"
                    title="Download"
                  >
                    <span className="material-symbols-outlined text-sm">
                      download
                    </span>
                  </button>
                  <button
                    className="p-2 text-gray-600 hover:text-red-600 rounded-full hover:bg-gray-100 transition-all duration-300 transform hover:scale-110"
                    title="Delete"
                  >
                    <span className="material-symbols-outlined text-sm">
                      delete
                    </span>
                  </button>
                </div>
              </td>
            </tr>
            <tr className="border-b hover:bg-gray-50 transition-all duration-300">
              <td className="px-6 py-4 font-medium">customer_feedback.csv</td>
              <td className="px-6 py-4">2023-11-28</td>
              <td className="px-6 py-4">3.5 MB</td>
              <td className="px-6 py-4">PDF</td>
              <td className="px-6 py-4">
                <div className="flex gap-3">
                  <button
                    className="p-2 text-gray-600 hover:text-primary-600 rounded-full hover:bg-gray-100 transition-all duration-300 transform hover:scale-110"
                    title="View"
                  >
                    <span className="material-symbols-outlined text-sm">
                      visibility
                    </span>
                  </button>
                  <button
                    className="p-2 text-gray-600 hover:text-primary-600 rounded-full hover:bg-gray-100 transition-all duration-300 transform hover:scale-110"
                    title="Download"
                  >
                    <span className="material-symbols-outlined text-sm">
                      download
                    </span>
                  </button>
                  <button
                    className="p-2 text-gray-600 hover:text-red-600 rounded-full hover:bg-gray-100 transition-all duration-300 transform hover:scale-110"
                    title="Delete"
                  >
                    <span className="material-symbols-outlined text-sm">
                      delete
                    </span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Recent;
