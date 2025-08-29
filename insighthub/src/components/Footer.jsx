import React from 'react'

const Footer = () => {
  return (
    <footer className="mt-16 text-center text-gray-500 text-sm py-6">
	    <p className="text-base">© 2023 Data Insight Generator. Powered by Gemini AI.</p>
	    <div className="flex justify-center gap-6 mt-3">
	      <a href="#1" className="hover:text-primary-600 transition-all duration-300 transform hover:scale-105">Privacy Policy</a>
	      <a href="#2" className="hover:text-primary-600 transition-all duration-300 transform hover:scale-105">Terms of Service</a>
	      <a href="#3" className="hover:text-primary-600 transition-all duration-300 transform hover:scale-105">Contact</a>
	    </div>
	  </footer>
  )
}

export default Footer
