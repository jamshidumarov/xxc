export default function Spinner({ size = 8 }) {
  return (
    <div className="flex justify-center items-center py-12">
      <div
        className={`w-${size} h-${size} border-4 border-t-transparent rounded-full animate-spin`}
        style={{ borderColor: '#E0D5C8', borderTopColor: '#8B5E3C' }}
      />
    </div>
  )
}
