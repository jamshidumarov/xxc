export default function Spinner({ size = 8 }) {
  return (
    <div className="flex justify-center items-center py-12">
      <div
        className={`w-${size} h-${size} border-4 border-t-transparent rounded-full animate-spin`}
        style={{ borderColor: '#D5DFDB', borderTopColor: '#2F6F73' }}
      />
    </div>
  )
}
