import axios from "axios";

// Base URLs
const API_BASE_URL_UPLOAD = "http://localhost:8081";
const API_BASE_URL_REPORT = "http://localhost:8083";
const API_BASE_URL_INSIGHT = "http://localhost:8082";

// ------------------ Report APIs ------------------

// Get images (base64) for a report
export const getReportImages = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL_REPORT}/api/report/${id}/data`);
    return response.data; // Array of base64 strings
  } catch (error) {
    throw error;
  }
};

// Download PDF report
export const downloadReportPdf = (id) => {
  const url = `${API_BASE_URL_REPORT}/api/report/${id}/pdf`;
  window.open(url, "_blank");
};

// ------------------ File Upload API ------------------

export const uploadFiles = async (files) => {
  const formData = new FormData();
  files.forEach((file) => formData.append("files", file));

  try {
    const response = await axios.post(`${API_BASE_URL_UPLOAD}/files`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    const cleanedResponse = await axios.post(`${API_BASE_URL_UPLOAD}/clean/${response.data[0].fileId}`);

    return cleanedResponse.data; // could be { reportId: "..." } depending on backend
  } catch (error) {
    console.error("Error uploading files:", error);
    throw error;
  }
};

// ------------------ Delete Reports APIs ------------------

export const deleteReport = async (reportId) => {
  try {
    await axios.delete(`${API_BASE_URL_REPORT}/api/reports/${reportId}`);
    alert("Report deleted successfully");
  } catch (error) {
    console.error("Failed to delete report:", error);
    alert("Failed to delete report");
  }
};

// Get Status of a report
export const getReportStatus = async (reportId) => {
  const res = await fetch(`${API_BASE_URL_INSIGHT}/api/insights/${reportId}/status`);
  if (!res.ok) throw new Error("Failed to fetch report status");
  return await res.json(); // returns { status: "PROCESSING" | "DONE" | "FAILED" }
};

