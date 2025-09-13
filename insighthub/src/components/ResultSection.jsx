import React, { useState, useEffect, useCallback } from "react";
import {
  getReportImages,
  downloadReportPdf,
  uploadFiles,
  getReportStatus,
} from "../api";

const ResultSection = ({ reportId }) => {
  const [insights, setInsights] = useState([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [status, setStatus] = useState("PROCESSING"); // new

  function formatFilename(filename) {
    const nameWithoutExt = filename.replace(/\.[^/.]+$/, "");
    const nameWithSpaces = nameWithoutExt.replace(/_/g, " ");
    return nameWithSpaces.replace(/\b\w/g, (char) => char.toUpperCase());
  }

  const fetchInsights = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getReportImages(reportId);
      setInsights(data || []);
    } catch (error) {
      console.error("Error fetching insights:", error);
      setInsights([]);
    } finally {
      setLoading(false);
    }
  }, [reportId]);

  // Polling for file readiness
  useEffect(() => {
    let interval;
    const pollStatus = async () => {
      try {
        const response = await getReportStatus(reportId); // backend should return {status: "PROCESSING"|"DONE"}
        setStatus(response.status);
        if (response.status === "DONE") {
          clearInterval(interval);
          await fetchInsights();
        }
      } catch (err) {
        console.error("Status check failed:", err);
      }
    };

    if (reportId) {
      interval = setInterval(pollStatus, 5000); // poll every 5 seconds
      pollStatus(); // immediate call
    }

    return () => clearInterval(interval);
  }, [reportId, fetchInsights]);

  const handleFileUpload = async (e) => {
    const files = Array.from(e.target.files);
    if (files.length === 0) return;

    setUploading(true);
    try {
      await uploadFiles(files);
      // Reset status to processing
      setStatus("PROCESSING");
    } catch (error) {
      console.error("Upload failed:", error);
    } finally {
      setUploading(false);
    }
  };

  const handleDownloadPdf = () => {
    if (!reportId) {
      alert("No report available for download.");
      return;
    }
    downloadReportPdf(reportId);
  };

  const handleImageDownload = (item) => {
    if (!item?.image) return;
    const link = document.createElement("a");
    link.href = item.image;
    link.download = "insight.png";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="w-full md:w-1/2 lg:w-3/5">
      <div className="bg-gradient-to-br from-gray-50 to-gray-100 rounded-xl p-6 h-full shadow-inner">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800">Data Insights</h2>
          <div className="flex gap-2">
            <button
              onClick={handleDownloadPdf}
              className="p-3 text-gray-600 hover:text-primary-600 rounded-full hover:bg-white transition-all duration-300 shadow-sm hover:shadow-md transform hover:scale-110"
            >
              <span className="material-symbols-outlined">download</span>
            </button>
            <input
              type="file"
              multiple
              onChange={handleFileUpload}
              disabled={uploading || status === "PROCESSING"}
              className="hidden"
              id="file-upload"
            />
            <label
              htmlFor="file-upload"
              className="p-3 text-gray-600 hover:text-primary-600 rounded-full hover:bg-white transition-all duration-300 shadow-sm hover:shadow-md transform hover:scale-110 cursor-pointer"
            >
              <span className="material-symbols-outlined">upload</span>
            </label>
          </div>
        </div>

        <div className="mb-6">
          <div className="flex gap-3 mb-4">
            <button
              className={`px-5 py-2 rounded-full transition-all duration-300 shadow-md transform hover:scale-105 ${
                status === "PROCESSING" || loading || uploading
                  ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                  : "bg-gradient-to-r from-primary-500 to-primary-600 text-white hover:from-primary-600 hover:to-primary-700 shadow-lg"
              }`}
              onClick={() => fetchInsights()}
              disabled={status === "PROCESSING" || loading || uploading}
            >
              All Insights
            </button>
            <button
              onClick={handleDownloadPdf}
              className={`px-5 py-2 rounded-full transition-all duration-300 shadow-md transform hover:scale-105 ${
                status === "PROCESSING"
                  ? "bg-gray-300 text-gray-500 cursor-not-allowed"
                  : "bg-white text-gray-700 hover:bg-gray-100 shadow"
              }`}
              disabled={status === "PROCESSING"}
            >
              PDF Format
            </button>
          </div>
        </div>

        <div className="space-y-6 overflow-y-auto max-h-[600px] pr-2 custom-scrollbar">
          {status === "PROCESSING" || loading || uploading ? (
            <div className="text-center text-gray-500 mt-20">Processing...</div>
          ) : insights.length === 0 ? (
            <div className="text-center text-gray-500 mt-20">
              No data available
            </div>
          ) : (
            insights.map((item, idx) => (
              <div
                key={idx}
                className="bg-white p-5 rounded-xl shadow-md hover:shadow-lg transition-all duration-300 border border-gray-100 transform hover:-translate-y-1"
              >
                <div className="flex justify-between items-start mb-3">
                  <h3 className="font-semibold text-lg">{`Insight ${
                    idx + 1
                  }`}</h3>
                  <div className="flex gap-1">
                    <button
                      className="p-2 text-gray-400 hover:text-primary-500 rounded-full hover:bg-gray-50 transition-all duration-300 transform hover:scale-110"
                      title="Download Image"
                      onClick={() => handleImageDownload(item)}
                    >
                      <span className="material-symbols-outlined text-sm">
                        image_download
                      </span>
                    </button>
                  </div>
                </div>
                <div className="h-64 w-full bg-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
                  {item ? (
                    <img
                      src={item.image}
                      alt={`Insight ${idx + 1}`}
                      className="w-full object-contain"
                    />
                  ) : (
                    <span className="material-symbols-outlined text-6xl text-primary-300 animate-pulse">
                      insert_chart
                    </span>
                  )}
                </div>
                <p className="mt-3 text-sm text-gray-600">
                  {formatFilename(item.title) || "No description available"}
                </p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default ResultSection;
